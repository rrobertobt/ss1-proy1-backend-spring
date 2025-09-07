package usac.cunoc.bpmn.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for enabling 2FA - matches PDF specification exactly
 */
@Data
@Schema(description = "Enable 2FA request data")
public class Enable2FARequestDto {

    @NotBlank(message = "Password is required")
    @Schema(description = "User password for verification", example = "SecurePass123!")
    private String password;
}