package usac.cunoc.bpmn.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for disable 2FA response - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Disable 2FA response data")
public class Disable2FAResponseDto {

    @Schema(description = "Whether 2FA is enabled", example = "false")
    private Boolean is_2fa_enabled;
}