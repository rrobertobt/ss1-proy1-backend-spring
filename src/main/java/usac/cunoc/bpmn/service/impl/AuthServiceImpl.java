package usac.cunoc.bpmn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usac.cunoc.bpmn.dto.auth.*;
import usac.cunoc.bpmn.entity.PasswordResetToken;
import usac.cunoc.bpmn.entity.User;
import usac.cunoc.bpmn.entity.UserType;
import usac.cunoc.bpmn.repository.GenderRepository;
import usac.cunoc.bpmn.repository.PasswordResetTokenRepository;
import usac.cunoc.bpmn.repository.UserRepository;
import usac.cunoc.bpmn.repository.UserTypeRepository;
import usac.cunoc.bpmn.security.JwtUtil;
import usac.cunoc.bpmn.service.AuthService;
import usac.cunoc.bpmn.service.EmailService;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

/**
 * Authentication service implementation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserTypeRepository userTypeRepository;
    private final GenderRepository genderRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    @Override
    @Transactional
    public RegisterResponseDto register(RegisterRequestDto request) {
        // Validate unique constraints matching database constraints
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Get default user type "Cliente" as per database initial data
        UserType clientUserType = userTypeRepository.findByName("Cliente")
                .orElseThrow(() -> new RuntimeException("Tipo de usuario por defecto no encontrado"));

        // Create user entity
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setBirthDate(request.getBirthDate());
        user.setPhone(request.getPhone());
        user.setUserType(clientUserType);

        // Set gender if provided
        if (request.getGenderId() != null) {
            user.setGender(genderRepository.findById(request.getGenderId())
                    .orElseThrow(() -> new RuntimeException("Género no encontrado")));
        }

        // Generate 6-digit verification code (always 6 digits)
        String verificationCode = generateSixDigitCode();
        user.setTwoFactorCode(verificationCode);
        user.setTwoFactorCodeExpires(LocalDateTime.now().plusMinutes(15));

        User savedUser = userRepository.save(user);

        // Send verification email
        try {
            emailService.sendVerificationEmail(savedUser.getEmail(), verificationCode);
        } catch (Exception e) {
            log.error("Failed to send verification email", e);
            // Don't fail registration if email fails
        }

        return new RegisterResponseDto(savedUser.getId(), savedUser.getEmail(), true);
    }

    @Override
    @Transactional
    public VerifyEmailResponseDto verifyEmail(VerifyEmailRequestDto request) {
        User user = userRepository.findByEmailAndValidTwoFactorCode(request.getEmail(), request.getVerificationCode())
                .orElseThrow(() -> new RuntimeException("Código de verificación inválido o expirado"));

        user.setIsVerified(true);
        user.setTwoFactorCode(null);
        user.setTwoFactorCodeExpires(null);
        userRepository.save(user);

        return new VerifyEmailResponseDto(true);
    }

    @Override
    @Transactional
    public LoginResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByUsernameOrEmail(request.getLogin())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        // Check account status in order of database constraints
        if (user.getIsActive() == null || !user.getIsActive()) {
            throw new RuntimeException("La cuenta está desactivada");
        }

        if (user.getIsBanned() != null && user.getIsBanned()) {
            throw new RuntimeException("La cuenta está suspendida");
        }

        if (user.getIsVerified() == null || !user.getIsVerified()) {
            throw new RuntimeException("Email no verificado");
        }

        // Check account lock (database field locked_until)
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Cuenta temporalmente bloqueada");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));

            // Reset failed attempts on successful login
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            user.setLastLogin(LocalDateTime.now());

            if (user.getIs2faEnabled() != null && user.getIs2faEnabled()) {
                // Generate 2FA code
                String twoFactorCode = generateSixDigitCode();
                user.setTwoFactorCode(twoFactorCode);
                user.setTwoFactorCodeExpires(LocalDateTime.now().plusMinutes(5));
                userRepository.save(user);

                // Send 2FA code
                try {
                    emailService.send2FACode(user.getEmail(), twoFactorCode);
                } catch (Exception e) {
                    log.error("Failed to send 2FA code", e);
                    throw new RuntimeException("Error enviando código 2FA");
                }

                // Return response requiring 2FA
                LoginResponseDto.UserInfoDto userInfo = new LoginResponseDto.UserInfoDto(
                        user.getId(), user.getUsername(), user.getEmail(),
                        user.getFirstName(), user.getLastName(),
                        user.getUserType().getName(), user.getIs2faEnabled());

                return new LoginResponseDto(null, null, userInfo, true);
            } else {
                userRepository.save(user);
                return generateLoginResponse(user);
            }

        } catch (BadCredentialsException | DisabledException e) {
            // Increment failed attempts as per database constraint
            Integer currentAttempts = user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() : 0;
            user.setFailedLoginAttempts(currentAttempts + 1);

            // Lock account after 5 failed attempts
            if (user.getFailedLoginAttempts() >= 5) {
                user.setLockedUntil(LocalDateTime.now().plusMinutes(30));
            }

            userRepository.save(user);
            throw new RuntimeException("Credenciales inválidas");
        }
    }

    @Override
    @Transactional
    public LoginResponseDto verify2FA(Verify2FARequestDto request) {
        User user = userRepository.findByEmailAndValidTwoFactorCode(request.getEmail(), request.getCode())
                .orElseThrow(() -> new RuntimeException("Código 2FA inválido o expirado"));

        user.setTwoFactorCode(null);
        user.setTwoFactorCodeExpires(null);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return generateLoginResponse(user);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Delete existing tokens for user
        passwordResetTokenRepository.deleteByUser(user);

        // Create new reset token with database constraints
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiresAt(LocalDateTime.now().plusHours(1));
        resetToken.setUsed(false);

        passwordResetTokenRepository.save(resetToken);

        // Send reset email
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), token);
        } catch (Exception e) {
            log.error("Failed to send password reset email", e);
            throw new RuntimeException("Error enviando email de recuperación");
        }
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequestDto request) {
        PasswordResetToken resetToken = passwordResetTokenRepository
                .findValidToken(request.getToken(), LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Token de reset inválido o expirado"));

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);

        // Mark token as used with database fields
        resetToken.setUsed(true);
        resetToken.setUsedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(resetToken);
    }

    @Override
    public RefreshTokenResponseDto refreshToken(RefreshTokenRequestDto request) {
        try {
            String username = jwtUtil.extractUsername(request.getRefreshToken());
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(request.getRefreshToken(), userDetails)) {
                String newAccessToken = jwtUtil.generateToken(userDetails);
                String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

                return new RefreshTokenResponseDto(newAccessToken, newRefreshToken);
            } else {
                throw new RuntimeException("Refresh token inválido");
            }
        } catch (Exception e) {
            throw new RuntimeException("Refresh token inválido");
        }
    }

    @Override
    public void logout(LogoutRequestDto request) {
        // Validate refresh token format
        try {
            String username = jwtUtil.extractUsername(request.getRefreshToken());
            if (username == null || username.trim().isEmpty()) {
                throw new RuntimeException("Token inválido");
            }
        } catch (Exception e) {
            throw new RuntimeException("Token inválido");
        }
    }

    private LoginResponseDto generateLoginResponse(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        LoginResponseDto.UserInfoDto userInfo = new LoginResponseDto.UserInfoDto(
                user.getId(), user.getUsername(), user.getEmail(),
                user.getFirstName(), user.getLastName(),
                user.getUserType().getName(), // Keep Spanish value from database
                user.getIs2faEnabled() != null ? user.getIs2faEnabled() : false);

        return new LoginResponseDto(accessToken, refreshToken, userInfo, false);
    }

    // Generate proper 6-digit code that respects database constraint
    private String generateSixDigitCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Ensures 6 digits
        return String.valueOf(code);
    }
}