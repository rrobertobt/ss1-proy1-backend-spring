package usac.cunoc.bpmn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
 * Authentication service - 100% compliant with PDF JSON structure and BD schema
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
        // Validate UNIQUE constraints exactly as BD defines
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Get "Cliente" userType exactly as stored in BD initial data
        UserType clientUserType = userTypeRepository.findByName("Cliente")
                .orElseThrow(() -> new RuntimeException("Default user type not found"));

        // Create user with exact BD defaults
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setBirthDate(request.getBirthDate());
        user.setPhone(request.getPhone());
        user.setUserType(clientUserType);

        // Set gender if provided (FK to gender table)
        if (request.getGenderId() != null) {
            user.setGender(genderRepository.findById(request.getGenderId())
                    .orElseThrow(() -> new RuntimeException("Gender not found")));
        }

        // Generate 6-digit code for verification (BD constraint: VARCHAR(6))
        String verificationCode = generateExactSixDigitCode();
        user.setTwoFactorCode(verificationCode);
        user.setTwoFactorCodeExpires(LocalDateTime.now().plusMinutes(15));

        User savedUser = userRepository.save(user);

        // Send verification email (non-blocking)
        try {
            emailService.sendVerificationEmail(savedUser.getEmail(), verificationCode);
        } catch (Exception e) {
            log.error("Failed to send verification email", e);
            // Continue - don't fail registration for email issues
        }

        // Return exact PDF JSON structure
        return new RegisterResponseDto(savedUser.getId(), savedUser.getEmail(), true);
    }

    @Override
    @Transactional
    public VerifyEmailResponseDto verifyEmail(VerifyEmailRequestDto request) {
        User user = userRepository.findByEmailAndValidTwoFactorCode(request.getEmail(), request.getVerificationCode())
                .orElseThrow(() -> new RuntimeException("Invalid verification code or code expired"));

        // Update BD fields exactly as schema defines
        user.setIsVerified(true);
        user.setTwoFactorCode(null);
        user.setTwoFactorCodeExpires(null);
        userRepository.save(user);

        // Return exact PDF JSON structure
        return new VerifyEmailResponseDto(true);
    }

    @Override
    @Transactional
    public LoginResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByUsernameOrEmail(request.getLogin())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        // Validate BD boolean constraints exactly
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new RuntimeException("Account is deactivated");
        }

        if (Boolean.TRUE.equals(user.getIsBanned())) {
            throw new RuntimeException("Account is banned");
        }

        if (!Boolean.TRUE.equals(user.getIsVerified())) {
            throw new RuntimeException("Email not verified");
        }

        // Check BD field locked_until
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Account is temporarily locked");
        }

        try {
            // Spring Security authentication
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));

            // Reset failed attempts (BD field failed_login_attempts)
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            user.setLastLogin(LocalDateTime.now());

            // Check BD field is_2fa_enabled
            if (Boolean.TRUE.equals(user.getIs2faEnabled())) {
                return handle2FAFlow(user);
            } else {
                userRepository.save(user);
                return generateSuccessLogin(user);
            }

        } catch (BadCredentialsException e) {
            handleFailedLoginAttempt(user);
            throw new RuntimeException("Invalid credentials");
        }
    }

    @Override
    @Transactional
    public LoginResponseDto verify2FA(Verify2FARequestDto request) {
        User user = userRepository.findByEmailAndValidTwoFactorCode(request.getEmail(), request.getCode())
                .orElseThrow(() -> new RuntimeException("Invalid 2FA code or code expired"));

        // Clear 2FA fields (BD schema)
        user.setTwoFactorCode(null);
        user.setTwoFactorCodeExpires(null);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return generateSuccessLogin(user);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Clean existing tokens (BD constraint)
        passwordResetTokenRepository.deleteByUser(user);

        // Create token following BD schema exactly
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token); // BD: VARCHAR(255) NOT NULL UNIQUE
        resetToken.setUser(user); // BD: user_id INTEGER NOT NULL REFERENCES user(id)
        resetToken.setExpiresAt(LocalDateTime.now().plusHours(1)); // BD: expires_at TIMESTAMP NOT NULL
        resetToken.setUsed(false); // BD: used BOOLEAN DEFAULT false

        passwordResetTokenRepository.save(resetToken);

        // Send reset email
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), token);
        } catch (Exception e) {
            log.error("Failed to send password reset email", e);
            throw new RuntimeException("Failed to send reset email");
        }
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequestDto request) {
        PasswordResetToken resetToken = passwordResetTokenRepository
                .findValidToken(request.getToken(), LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        User user = resetToken.getUser();

        // Update password and reset security fields
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);

        // Mark token as used (BD fields)
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

                // Return exact PDF JSON structure
                return new RefreshTokenResponseDto(newAccessToken, newRefreshToken);
            } else {
                throw new RuntimeException("Invalid refresh token");
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token");
        }
    }

    @Override
    public void logout(LogoutRequestDto request) {
        try {
            String username = jwtUtil.extractUsername(request.getRefreshToken());
            if (username == null || username.trim().isEmpty()) {
                throw new RuntimeException("Invalid token");
            }
            // In production: add token to blacklist
        } catch (Exception e) {
            throw new RuntimeException("Invalid token");
        }
    }

    // PRIVATE HELPER METHODS

    private LoginResponseDto handle2FAFlow(User user) {
        String twoFactorCode = generateExactSixDigitCode();
        user.setTwoFactorCode(twoFactorCode);
        user.setTwoFactorCodeExpires(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        try {
            emailService.send2FACode(user.getEmail(), twoFactorCode);
        } catch (Exception e) {
            log.error("Failed to send 2FA code", e);
            throw new RuntimeException("Failed to send 2FA code");
        }

        // Return exact PDF JSON structure for 2FA required
        LoginResponseDto.UserInfoDto userInfo = new LoginResponseDto.UserInfoDto(
                user.getId(), user.getUsername(), user.getEmail(),
                user.getFirstName(), user.getLastName(),
                user.getUserType().getName(), // Exact BD value: "Cliente" or "Administrador"
                user.getIs2faEnabled());

        // Per PDF: tokens are null when 2FA required, requires2fa = true
        return new LoginResponseDto(null, null, userInfo, true);
    }

    private LoginResponseDto generateSuccessLogin(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        // Return exact PDF JSON structure for successful login
        LoginResponseDto.UserInfoDto userInfo = new LoginResponseDto.UserInfoDto(
                user.getId(), user.getUsername(), user.getEmail(),
                user.getFirstName(), user.getLastName(),
                user.getUserType().getName(), // Exact BD value: "Cliente" or "Administrador"
                Boolean.TRUE.equals(user.getIs2faEnabled()));

        // Per PDF: tokens present when login successful, requires2fa = false
        return new LoginResponseDto(accessToken, refreshToken, userInfo, false);
    }

    private void handleFailedLoginAttempt(User user) {
        Integer currentAttempts = user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() : 0;
        user.setFailedLoginAttempts(currentAttempts + 1);

        // Lock account after 5 failed attempts (business rule)
        if (user.getFailedLoginAttempts() >= 5) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(30));
        }

        userRepository.save(user);
    }

    private String generateExactSixDigitCode() {
        Random random = new Random();
        // Ensures exactly 6 digits (100000 to 999999) - respects BD VARCHAR(6)
        // constraint
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}