package usac.cunoc.bpmn.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for user login request
 */
@Data
@Schema(description = "User login request data")
public class LoginRequestDto {

    @NotBlank(message = "Login is required")
    @Schema(description = "Username or email", example = "johndoe123")
    private String login;

    @NotBlank(message = "Password is required")
    @Schema(description = "User password", example = "SecurePass123!")
    private String password;
}