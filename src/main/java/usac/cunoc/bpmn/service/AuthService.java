package usac.cunoc.bpmn.service;

import usac.cunoc.bpmn.dto.auth.*;

/**
 * Authentication service interface
 */
public interface AuthService {

    /**
     * Register a new user
     */
    RegisterResponseDto register(RegisterRequestDto request);

    /**
     * Verify user email
     */
    VerifyEmailResponseDto verifyEmail(VerifyEmailRequestDto request);

    /**
     * User login
     */
    LoginResponseDto login(LoginRequestDto request);

    /**
     * Verify 2FA code
     */
    LoginResponseDto verify2FA(Verify2FARequestDto request);

    /**
     * Send forgot password email
     */
    void forgotPassword(ForgotPasswordRequestDto request);

    /**
     * Reset password
     */
    void resetPassword(ResetPasswordRequestDto request);

    /**
     * Refresh authentication tokens
     */
    refreshTokenResponseDto refreshToken(refreshTokenRequestDto request);

    /**
     * Logout user
     */
    void logout(LogoutRequestDto request);
}