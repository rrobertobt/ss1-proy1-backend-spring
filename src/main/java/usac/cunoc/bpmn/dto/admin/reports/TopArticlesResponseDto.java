package usac.cunoc.bpmn.dto.admin.reports;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for top articles report response - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Top articles report response data")
public class TopArticlesResponseDto {

    @Schema(description = "Report period", example = "30 days")
    private String period;

    @Schema(description = "List of top selling articles")
    private List<TopArticleDto> topArticles;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Top article information")
    public static class TopArticleDto {
        @Schema(description = "Article rank", example = "1")
        private Integer rank;

        @Schema(description = "Article details")
        private ArticleDto article;

        @Schema(description = "Total units sold", example = "45")
        private Integer totalSold;

        @Schema(description = "Total revenue generated", example = "2250.75")
        private BigDecimal totalRevenue;

        @Schema(description = "Average rating", example = "4.5")
        private BigDecimal averageRating;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Article basic information")
    public static class ArticleDto {
        @Schema(description = "Article ID", example = "1")
        private Integer id;

        @Schema(description = "Article title", example = "Abbey Road")
        private String title;

        @Schema(description = "Artist name", example = "The Beatles")
        private String artist;

        @Schema(description = "Article type", example = "vinyl")
        private String type;

        @Schema(description = "Article image URL", example = "https://example.com/image.jpg")
        private String imageUrl;
    }
}