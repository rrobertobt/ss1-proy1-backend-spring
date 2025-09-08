package usac.cunoc.bpmn.dto.admin.reports;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

/**
 * DTO for sales report request - matches PDF specification exactly
 */
@Data
@Schema(description = "Sales report request parameters")
public class SalesReportRequestDto {

    @NotNull(message = "Start date is required")
    @Schema(description = "Report start date", example = "2024-01-01")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Schema(description = "Report end date", example = "2024-01-31")
    private LocalDate endDate;

    @Schema(description = "Group by criteria", example = "daily", allowableValues = { "daily", "weekly", "monthly" })
    private String groupBy = "daily";
}