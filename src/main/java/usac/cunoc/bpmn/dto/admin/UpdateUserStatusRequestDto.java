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

    @NotNull(message = "isActive status is required")
    @Schema(description = "Whether user account is active", example = "true")
    private Boolean isActive;

    @NotNull(message = "isBanned status is required")
    @Schema(description = "Whether user account is banned", example = "false")
    private Boolean isBanned;

    @Schema(description = "Reason for status change", example = "Violación de términos de servicio")
    private String reason;
}