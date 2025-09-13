package usac.cunoc.bpmn.dto.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Catalog article DTO - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Article information for catalog display")
public class CatalogArticleDto {

    @Schema(description = "Article ID", example = "1")
    private Integer id;

    @Schema(description = "Article title", example = "Abbey Road")
    private String title;

    @Schema(description = "Artist information")
    private ArtistDto artist;

    @Schema(description = "Article type", example = "vinyl", allowableValues = { "vinyl", "cassette", "cd" })
    private String type;

    @Schema(description = "Article price", example = "29.99")
    private BigDecimal price;

    @Schema(description = "Currency information")
    private CurrencyDto currency;

    @Schema(description = "Music genre information")
    private GenreDto genre;

    @Schema(description = "Article image URL", example = "https://example.com/images/abbey-road.jpg")
    private String image_url;

    @Schema(description = "Average rating", example = "4.5")
    private BigDecimal average_rating;

    @Schema(description = "Total ratings count", example = "128")
    private Integer total_ratings;

    @Schema(description = "Stock quantity", example = "15")
    private Integer stock_quantity;

    @Schema(description = "Is article available", example = "true")
    private Boolean is_available;

    @Schema(description = "Is article in preorder", example = "false")
    private Boolean is_preorder;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Artist information")
    public static class ArtistDto {
        @Schema(description = "Artist ID", example = "1")
        private Integer id;

        @Schema(description = "Artist name", example = "The Beatles")
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Currency information")
    public static class CurrencyDto {
        @Schema(description = "Currency code", example = "USD")
        private String code;

        @Schema(description = "Currency symbol", example = "$")
        private String symbol;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Genre information")
    public static class GenreDto {
        @Schema(description = "Genre ID", example = "1")
        private Integer id;

        @Schema(description = "Genre name", example = "Rock")
        private String name;
    }
}