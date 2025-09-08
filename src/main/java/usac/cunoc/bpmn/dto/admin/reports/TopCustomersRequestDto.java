package usac.cunoc.bpmn.dto.admin.reports;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * DTO for top customers report request - matches PDF specification exactly
 */
@Data
@Schema(description = "Top customers report request parameters")
public class TopCustomersRequestDto {

    @Schema(description = "Period for analysis", example = "30", allowableValues = { "7", "30", "90", "365" })
    private String period = "30";

    @Min(value = 1, message = "Limit must be at least 1")
    @Max(value = 50, message = "Limit cannot exceed 50")
    @Schema(description = "Maximum number of customers to return", example = "10")
    private Integer limit = 10;
}