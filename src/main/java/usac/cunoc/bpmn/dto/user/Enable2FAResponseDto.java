package usac.cunoc.bpmn.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO for enable 2FA response - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Enable 2FA response data")
public class Enable2FAResponseDto {

    @Schema(description = "Whether 2FA is enabled", example = "true")
    private Boolean is2faEnabled;

    @Schema(description = "Backup codes for 2FA recovery")
    private List<String> backupCodes;
}