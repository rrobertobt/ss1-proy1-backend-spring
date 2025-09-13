package usac.cunoc.bpmn.dto.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usac.cunoc.bpmn.dto.common.PaginationDto;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Article ratings response DTO - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for article ratings")
public class ArticleRatingsResponseDto {

    @Schema(description = "Article ID", example = "1")
    private Integer article_id;

    @Schema(description = "Average rating", example = "4.25")
    private BigDecimal average_rating;

    @Schema(description = "Total ratings count", example = "128")
    private Integer total_ratings;

    @Schema(description = "Rating distribution by stars")
    private Map<String, Integer> rating_distribution;

    @Schema(description = "List of ratings")
    private List<ArticleRatingDto> ratings;

    @Schema(description = "Pagination information")
    private PaginationDto pagination;
}