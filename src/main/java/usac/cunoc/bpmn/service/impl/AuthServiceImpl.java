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
        // Validate unique constraints
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Get default user type (Cliente)
        UserType clientUserType = userTypeRepository.findByName("Cliente")
                .orElseThrow(() -> new RuntimeException("Default user type not found"));

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

        if (request.getGenderId() != null) {
            user.setGender(genderRepository.findById(request.getGenderId())
                    .orElseThrow(() -> new RuntimeException("Gender not found")));
        }

        // Generate verification code
        String verificationCode = generateVerificationCode();
        user.setTwoFactorCode(verificationCode);
        user.setTwoFactorCodeExpires(LocalDateTime.now().plusMinutes(15));

        User savedUser = userRepository.save(user);

        // Send verification email
        emailService.sendVerificationEmail(savedUser.getEmail(), verificationCode);

        return new RegisterResponseDto(savedUser.getId(), savedUser.getEmail(), true);
    }

    @Override
    @Transactional
    public VerifyEmailResponseDto verifyEmail(VerifyEmailRequestDto request) {
        User user = userRepository.findByEmailAndValidTwoFactorCode(request.getEmail(), request.getVerificationCode())
                .orElseThrow(() -> new RuntimeException("Invalid verification code or code expired"));

        user.setIsVerified(true);
        user.setTwoFactorCode(null);
        user.setTwoFactorCodeExpires(null);
        userRepository.save(user);

        return new VerifyEmailResponseDto(true);
    }

    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByUsernameOrEmail(request.getLogin())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!user.getIsActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        if (user.getIsBanned()) {
            throw new RuntimeException("Account is banned");
        }

        if (!user.getIsVerified()) {
            throw new RuntimeException("Email not verified");
        }

        // Check account lock
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Account is temporarily locked");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));

            // Reset failed attempts on successful login
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            user.setLastLogin(LocalDateTime.now());

            if (user.getIs2faEnabled()) {
                // Generate 2FA code
                String twoFactorCode = generateVerificationCode();
                user.setTwoFactorCode(twoFactorCode);
                user.setTwoFactorCodeExpires(LocalDateTime.now().plusMinutes(5));
                userRepository.save(user);

                // Send 2FA code
                emailService.send2FACode(user.getEmail(), twoFactorCode);

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

        } catch (BadCredentialsException e) {
            // Increment failed attempts
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

            // Lock account after 5 failed attempts
            if (user.getFailedLoginAttempts() >= 5) {
                user.setLockedUntil(LocalDateTime.now().plusMinutes(30));
            }

            userRepository.save(user);
            throw new RuntimeException("Invalid credentials");
        }
    }

    @Override
    @Transactional
    public LoginResponseDto verify2FA(Verify2FARequestDto request) {
        User user = userRepository.findByEmailAndValidTwoFactorCode(request.getEmail(), request.getCode())
                .orElseThrow(() -> new RuntimeException("Invalid 2FA code or code expired"));

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
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Delete existing tokens for user
        passwordResetTokenRepository.deleteByUser(user);

        // Create new reset token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiresAt(LocalDateTime.now().plusHours(1));

        passwordResetTokenRepository.save(resetToken);

        // Send reset email
        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequestDto request) {
        PasswordResetToken resetToken = passwordResetTokenRepository
                .findValidToken(request.getToken(), LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);

        // Mark token as used
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
                throw new RuntimeException("Invalid refresh token");
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token");
        }
    }

    @Override
    public void logout(LogoutRequestDto request) {
        // In a production environment, you would add the token to a blacklist
        // For now, we'll just validate that the token is valid
        try {
            String username = jwtUtil.extractUsername(request.getRefreshToken());
            if (username == null) {
                throw new RuntimeException("Invalid token");
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid token");
        }
    }

    private LoginResponseDto generateLoginResponse(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        LoginResponseDto.UserInfoDto userInfo = new LoginResponseDto.UserInfoDto(
                user.getId(), user.getUsername(), user.getEmail(),
                user.getFirstName(), user.getLastName(),
                user.getUserType().getName(), user.getIs2faEnabled());

        return new LoginResponseDto(accessToken, refreshToken, userInfo, false);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }
}