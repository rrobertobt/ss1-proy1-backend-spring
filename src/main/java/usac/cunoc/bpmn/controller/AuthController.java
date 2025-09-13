package usac.cunoc.bpmn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usac.cunoc.bpmn.dto.auth.*;
import usac.cunoc.bpmn.dto.common.ApiResponseDto;
import usac.cunoc.bpmn.service.AuthService;

/**
 * Authentication controller with responses matching PDF specification exactly
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and authorization operations")
public class AuthController {

        private final AuthService authService;

        @PostMapping("/register")
        @Operation(summary = "Register new user", description = "Register a new user account")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "User registered successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data")
        })
        public ResponseEntity<ApiResponseDto<RegisterResponseDto>> register(
                        @Valid @RequestBody RegisterRequestDto request) {

                RegisterResponseDto response = authService.register(request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponseDto.success("Usuario registrado exitosamente", response));
        }

        @PostMapping("/verify-email")
        @Operation(summary = "Verify email", description = "Verify user email with verification code")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Email verified successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid verification code")
        })
        public ResponseEntity<ApiResponseDto<VerifyEmailResponseDto>> verifyEmail(
                        @Valid @RequestBody VerifyEmailRequestDto request) {

                VerifyEmailResponseDto response = authService.verifyEmail(request);
                return ResponseEntity.ok(ApiResponseDto.success("Email verificado exitosamente", response));
        }

        @PostMapping("/login")
        @Operation(summary = "User login", description = "Authenticate user and return JWT tokens")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Login successful"),
                        @ApiResponse(responseCode = "401", description = "Invalid credentials")
        })
        public ResponseEntity<ApiResponseDto<LoginResponseDto>> login(
                        @Valid @RequestBody LoginRequestDto request) {

                LoginResponseDto response = authService.login(request);

                // Si requires2fa es true, no devolvemos mensaje "Login exitoso"
                if (Boolean.TRUE.equals(response.getRequires_2fa())) {
                        return ResponseEntity.ok(ApiResponseDto.success(null, response));
                } else {
                        return ResponseEntity.ok(ApiResponseDto.success(null, response));
                }
        }

        @PostMapping("/verify-2fa")
        @Operation(summary = "Verify 2FA code", description = "Verify two-factor authentication code")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "2FA verification successful"),
                        @ApiResponse(responseCode = "400", description = "Invalid 2FA code")
        })
        public ResponseEntity<ApiResponseDto<LoginResponseDto>> verify2FA(
                        @Valid @RequestBody Verify2FARequestDto request) {

                LoginResponseDto response = authService.verify2FA(request);
                return ResponseEntity.ok(ApiResponseDto.success(null, response));
        }

        @PostMapping("/forgot-password")
        @Operation(summary = "Forgot password", description = "Send password reset email")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Reset email sent successfully"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        public ResponseEntity<ApiResponseDto<Void>> forgotPassword(
                        @Valid @RequestBody ForgotPasswordRequestDto request) {

                authService.forgotPassword(request);
                return ResponseEntity.ok(ApiResponseDto.success("Se ha enviado un enlace de recuperación a tu email"));
        }

        @PostMapping("/reset-password")
        @Operation(summary = "Reset password", description = "Reset password using reset token")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Password reset successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid or expired token")
        })
        public ResponseEntity<ApiResponseDto<Void>> resetPassword(
                        @Valid @RequestBody ResetPasswordRequestDto request) {

                authService.resetPassword(request);
                return ResponseEntity.ok(ApiResponseDto.success("Contraseña restablecida exitosamente"));
        }

        @PostMapping("/refresh-token")
        @Operation(summary = "Refresh token", description = "Refresh JWT access token")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
                        @ApiResponse(responseCode = "401", description = "Invalid refresh token")
        })
        public ResponseEntity<ApiResponseDto<refreshTokenResponseDto>> refreshToken(
                        @Valid @RequestBody refreshTokenRequestDto request) {

                refreshTokenResponseDto response = authService.refreshToken(request);
                return ResponseEntity.ok(ApiResponseDto.success(null, response));
        }

        @PostMapping("/logout")
        @Operation(summary = "User logout", description = "Logout user and invalidate tokens")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Logout successful"),
                        @ApiResponse(responseCode = "400", description = "Invalid token")
        })
        public ResponseEntity<ApiResponseDto<Void>> logout(
                        @Valid @RequestBody LogoutRequestDto request) {

                authService.logout(request);
                return ResponseEntity.ok(ApiResponseDto.success("Sesión cerrada exitosamente"));
        }
}