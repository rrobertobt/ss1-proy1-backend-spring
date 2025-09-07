package usac.cunoc.bpmn.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user login response - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User login response data")
public class LoginResponseDto {

    @Schema(description = "JWT access token")
    private String accessToken;

    @Schema(description = "JWT refresh token")
    private String refreshToken;

    @Schema(description = "User information")
    private UserInfoDto user;

    @Schema(description = "Whether 2FA is required", example = "false")
    private Boolean requires2fa;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "User information according to PDF specification")
    public static class UserInfoDto {
        @Schema(description = "User ID", example = "1")
        private Integer id;

        @Schema(description = "Username", example = "johndoe123")
        private String username;

        @Schema(description = "Email address", example = "john.doe@example.com")
        private String email;

        @Schema(description = "First name", example = "John")
        private String firstName;

        @Schema(description = "Last name", example = "Doe")
        private String lastName;

        @Schema(description = "User type", example = "Cliente")
        private String userType;

        @Schema(description = "Whether 2FA is enabled", example = "false")
        private Boolean is2faEnabled;
    }
}