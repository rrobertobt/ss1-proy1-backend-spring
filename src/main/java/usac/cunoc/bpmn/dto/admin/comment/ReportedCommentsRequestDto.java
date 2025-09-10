package usac.cunoc.bpmn.dto.admin.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * DTO for reported comments request - matches PDF specification exactly
 */
@Data
@Schema(description = "Reported comments request parameters")
public class ReportedCommentsRequestDto {

    @Min(value = 1, message = "Page must be at least 1")
    @Schema(description = "Page number", example = "1")
    private Integer page = 1;

    @Min(value = 1, message = "Limit must be at least 1")
    @Max(value = 100, message = "Limit cannot exceed 100")
    @Schema(description = "Items per page", example = "10")
    private Integer limit = 10;

    @Schema(description = "Filter by comment status", example = "Reportado", allowableValues = { "Activo", "Eliminado",
            "Reportado" })
    private String status;
}