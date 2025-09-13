package usac.cunoc.bpmn.dto.admin.reports;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for top rated articles response - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Top rated articles response data")
public class TopRatedResponseDto {

    @Schema(description = "Rating criteria used")
    private CriteriaDto criteria;

    @Schema(description = "List of top rated articles")
    private List<TopRatedArticleDto> topRatedArticles;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Rating criteria")
    public static class CriteriaDto {
        @Schema(description = "Minimum ratings required", example = "5")
        private Integer min_ratings;

        @Schema(description = "Maximum results limit", example = "10")
        private Integer limit;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Top rated article information")
    public static class TopRatedArticleDto {
        @Schema(description = "Article rank", example = "1")
        private Integer rank;

        @Schema(description = "Article details")
        private ArticleDto article;

        @Schema(description = "Average rating", example = "4.8")
        private BigDecimal average_rating;

        @Schema(description = "Total number of ratings", example = "25")
        private Integer total_ratings;

        @Schema(description = "Total units sold", example = "150")
        private Integer total_sold;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Article basic information")
    public static class ArticleDto {
        @Schema(description = "Article ID", example = "1")
        private Integer id;

        @Schema(description = "Article title", example = "Dark Side of the Moon")
        private String title;

        @Schema(description = "Artist name", example = "Pink Floyd")
        private String artist;

        @Schema(description = "Article type", example = "vinyl")
        private String type;

        @Schema(description = "Article image URL", example = "https://example.com/image.jpg")
        private String image_url;
    }
}