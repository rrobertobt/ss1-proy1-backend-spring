package usac.cunoc.bpmn.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for reset password request
 */
@Data
@Schema(description = "Reset password request data")
public class ResetPasswordRequestDto {

    @NotBlank(message = "Token is required")
    @Schema(description = "Password reset token", example = "abc123def456")
    private String token;

    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Schema(description = "New password", example = "NewSecurePass123!")
    private String newPassword;
}