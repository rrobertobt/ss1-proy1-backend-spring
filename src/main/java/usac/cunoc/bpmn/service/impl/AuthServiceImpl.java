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
 * Authentication service implementation with complete database validation
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
        // Validate unique constraints exactly as database defines them
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Get user type "Cliente" exactly as stored in database
        UserType clientUserType = userTypeRepository.findByName("Cliente")
                .orElseThrow(() -> new RuntimeException("Tipo de usuario 'Cliente' no encontrado en la base de datos"));

        // Create user entity respecting all database constraints
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail().toLowerCase()); // Normalize email
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setBirthDate(request.getBirthDate());
        user.setPhone(request.getPhone());
        user.setUserType(clientUserType);

        // Set default values as per database schema
        user.setIsActive(true);
        user.setIsVerified(false);
        user.setFailedLoginAttempts(0);
        user.setIs2faEnabled(false);
        user.setDeletedCommentsCount(0);
        user.setIsBanned(false);
        user.setTotalSpent(java.math.BigDecimal.ZERO);
        user.setTotalOrders(0);

        // Set gender if provided, validating against database
        if (request.getGenderId() != null) {
            user.setGender(genderRepository.findById(request.getGenderId())
                    .orElseThrow(() -> new RuntimeException("ID de género no válido")));
        }

        // Generate exactly 6-digit verification code as per database constraint
        String verificationCode = generateSecureSixDigitCode();
        user.setTwoFactorCode(verificationCode);
        user.setTwoFactorCodeExpires(LocalDateTime.now().plusMinutes(15));

        try {
            User savedUser = userRepository.save(user);

            // Send verification email
            emailService.sendVerificationEmail(savedUser.getEmail(), verificationCode);

            return new RegisterResponseDto(savedUser.getId(), savedUser.getEmail(), true);

        } catch (Exception e) {
            log.error("Error saving user to database", e);
            throw new RuntimeException("Error al registrar usuario: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public VerifyEmailResponseDto verifyEmail(VerifyEmailRequestDto request) {
        User user = userRepository.findByEmailAndValidTwoFactorCode(
                request.getEmail(),
                request.getVerificationCode(),
                LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Código de verificación inválido o expirado"));

        // Update user as per database triggers and constraints
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

        // Validate all database constraints for user status
        validateUserAccountStatus(user);

        try {
            // Authenticate with Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));

            // Reset failed attempts on successful login (database constraint)
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            user.setLastLogin(LocalDateTime.now());

            // Check if 2FA is enabled (database field is_2fa_enabled)
            if (Boolean.TRUE.equals(user.getIs2faEnabled())) {
                return handle2FALogin(user);
            } else {
                userRepository.save(user);
                return generateSuccessfulLoginResponse(user);
            }

        } catch (BadCredentialsException | DisabledException e) {
            handleFailedLogin(user);
            throw new RuntimeException("Credenciales inválidas");
        }
    }

    @Override
    @Transactional
    public LoginResponseDto verify2FA(Verify2FARequestDto request) {
        User user = userRepository.findByEmailAndValidTwoFactorCode(
                request.getEmail(),
                request.getCode(),
                LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Código 2FA inválido o expirado"));

        // Clear 2FA code and update login time
        user.setTwoFactorCode(null);
        user.setTwoFactorCodeExpires(null);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return generateSuccessfulLoginResponse(user);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Clean up existing tokens respecting database constraints
        passwordResetTokenRepository.deleteByUser(user);

        // Create token following database schema exactly
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiresAt(LocalDateTime.now().plusHours(1)); // Database allows this
        resetToken.setUsed(false); // Database default

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

        // Update password and reset security fields
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setFailedLoginAttempts(0); // Reset as per database constraint
        user.setLockedUntil(null);

        userRepository.save(user);

        // Mark token as used following database schema
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
            log.error("Error refreshing token", e);
            throw new RuntimeException("Refresh token inválido");
        }
    }

    @Override
    public void logout(LogoutRequestDto request) {
        // Validate token format and structure
        try {
            String username = jwtUtil.extractUsername(request.getRefreshToken());
            if (username == null || username.trim().isEmpty()) {
                throw new RuntimeException("Token inválido");
            }
            // In production, add token to blacklist here
        } catch (Exception e) {
            log.error("Error during logout", e);
            throw new RuntimeException("Token inválido");
        }
    }

    // Private helper methods

    private void validateUserAccountStatus(User user) {
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new RuntimeException("La cuenta está desactivada");
        }

        if (Boolean.TRUE.equals(user.getIsBanned())) {
            throw new RuntimeException("La cuenta está suspendida");
        }

        if (!Boolean.TRUE.equals(user.getIsVerified())) {
            throw new RuntimeException("Email no verificado. Por favor verifica tu email antes de iniciar sesión");
        }

        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Cuenta temporalmente bloqueada. Intenta nuevamente más tarde");
        }
    }

    private LoginResponseDto handle2FALogin(User user) {
        String twoFactorCode = generateSecureSixDigitCode();
        user.setTwoFactorCode(twoFactorCode);
        user.setTwoFactorCodeExpires(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        try {
            emailService.send2FACode(user.getEmail(), twoFactorCode);
        } catch (Exception e) {
            log.error("Failed to send 2FA code", e);
            throw new RuntimeException("Error enviando código 2FA");
        }

        LoginResponseDto.UserInfoDto userInfo = new LoginResponseDto.UserInfoDto(
                user.getId(), user.getUsername(), user.getEmail(),
                user.getFirstName(), user.getLastName(),
                user.getUserType().getName(), // Keep exact database value
                user.getIs2faEnabled());

        return new LoginResponseDto(null, null, userInfo, true);
    }

    private void handleFailedLogin(User user) {
        Integer currentAttempts = user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() : 0;
        user.setFailedLoginAttempts(currentAttempts + 1);

        // Lock account after 5 failed attempts as per business rules
        if (user.getFailedLoginAttempts() >= 5) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(30));
        }

        userRepository.save(user);
    }

    private LoginResponseDto generateSuccessfulLoginResponse(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        LoginResponseDto.UserInfoDto userInfo = new LoginResponseDto.UserInfoDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getUserType().getName(), // Preserva valor exacto de BD en español
                Boolean.TRUE.equals(user.getIs2faEnabled()));

        return new LoginResponseDto(accessToken, refreshToken, userInfo, false);
    }

    private String generateSecureSixDigitCode() {
        Random random = new Random();
        // Asegura exactamente 6 dígitos respetando constraint de BD
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}