package usac.cunoc.bpmn.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for disabling 2FA - matches PDF specification exactly
 */
@Data
@Schema(description = "Disable 2FA request data")
public class Disable2FARequestDto {

    @NotBlank(message = "Password is required")
    @Schema(description = "User password for verification", example = "SecurePass123!")
    private String password;

    @NotBlank(message = "2FA code is required")
    @Size(min = 6, max = 6, message = "2FA code must be 6 characters")
    @Schema(description = "Current 2FA code", example = "123456")
    private String two_factor_code;
}