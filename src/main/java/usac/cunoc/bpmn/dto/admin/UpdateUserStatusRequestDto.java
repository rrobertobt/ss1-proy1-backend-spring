package usac.cunoc.bpmn.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for updating user status - matches PDF specification exactly
 */
@Data
@Schema(description = "Update user status request data")
public class UpdateUserStatusRequestDto {

    @NotNull(message = "is_active status is required")
    @Schema(description = "Whether user account is active", example = "true")
    private Boolean is_active;

    @NotNull(message = "is_banned status is required")
    @Schema(description = "Whether user account is banned", example = "false")
    private Boolean is_banned;

    @Schema(description = "Reason for status change", example = "Violación de términos de servicio")
    private String reason;
}