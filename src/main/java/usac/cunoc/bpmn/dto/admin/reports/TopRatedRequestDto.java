package usac.cunoc.bpmn.dto.admin.reports;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * DTO for top rated articles request - matches PDF specification exactly
 */
@Data
@Schema(description = "Top rated articles request parameters")
public class TopRatedRequestDto {

    @Min(value = 1, message = "Minimum ratings must be at least 1")
    @Schema(description = "Minimum number of ratings required", example = "5")
    private Integer minRatings = 5;

    @Min(value = 1, message = "Limit must be at least 1")
    @Max(value = 50, message = "Limit cannot exceed 50")
    @Schema(description = "Maximum number of articles to return", example = "10")
    private Integer limit = 10;
}