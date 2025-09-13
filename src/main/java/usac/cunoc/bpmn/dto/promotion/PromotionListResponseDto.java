package usac.cunoc.bpmn.dto.promotion;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for promotion list - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing promotions list")
public class PromotionListResponseDto {

    @Schema(description = "List of available promotions")
    private List<PromotionDto> promotions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Promotion details")
    public static class PromotionDto {

        @Schema(description = "Promotion ID", example = "1")
        private Integer id;

        @Schema(description = "Promotion name", example = "Promoción Rock Clásico")
        private String name;

        @Schema(description = "Promotion type details")
        private promotion_typeDto promotion_type;

        @Schema(description = "Genre details (only for genre-based promotions)")
        private GenreDto genre;

        @Schema(description = "Discount percentage", example = "10.00")
        private BigDecimal discount_percentage;

        @Schema(description = "Maximum items allowed", example = "4")
        private Integer max_items;

        @Schema(description = "Promotion start date")
        private LocalDateTime start_date;

        @Schema(description = "Promotion end date")
        private LocalDateTime end_date;

        @Schema(description = "Whether promotion is active", example = "true")
        private Boolean is_active;

        @Schema(description = "Number of times used", example = "15")
        private Integer usage_count;

        @Schema(description = "List of eligible articles")
        private List<EligibleArticleDto> eligible_articles;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Promotion type details")
    public static class promotion_typeDto {

        @Schema(description = "Promotion type ID", example = "1")
        private Integer id;

        @Schema(description = "Promotion type name", example = "Por Genero")
        private String name;

        @Schema(description = "Maximum items for this type", example = "4")
        private Integer max_items;

        @Schema(description = "Default discount percentage", example = "10.00")
        private BigDecimal discount_percentage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Genre details")
    public static class GenreDto {

        @Schema(description = "Genre ID", example = "1")
        private Integer id;

        @Schema(description = "Genre name", example = "Rock")
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Eligible article details")
    public static class EligibleArticleDto {

        @Schema(description = "Article ID", example = "15")
        private Integer id;

        @Schema(description = "Article title", example = "Dark Side of the Moon")
        private String title;

        @Schema(description = "Artist name", example = "Pink Floyd")
        private String artist;

        @Schema(description = "Article price", example = "29.99")
        private BigDecimal price;

        @Schema(description = "Article image URL")
        private String image_url;
    }
}