package usac.cunoc.bpmn.dto.catalog;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Article detail response DTO - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detailed article information")
public class ArticleDetailResponseDto {

    @Schema(description = "Article ID", example = "1")
    private Integer id;

    @Schema(description = "Article title", example = "Abbey Road")
    private String title;

    @Schema(description = "Artist information")
    private ArtistDetailDto artist;

    @Schema(description = "Article type", example = "vinyl", allowableValues = { "vinyl", "cassette", "cd" })
    private String type;

    @Schema(description = "Article price", example = "29.99")
    private BigDecimal price;

    @Schema(description = "Currency information")
    private CurrencyDto currency;

    @Schema(description = "Music genre information")
    private GenreDto genre;

    @Schema(description = "Release date", example = "1969-09-26", type = "string", format = "date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate release_date;

    @Schema(description = "Article description")
    private String description;

    @Schema(description = "Physical dimensions", example = "12 x 12 inches")
    private String dimensions;

    @Schema(description = "Weight in grams", example = "180")
    private Integer weight_grams;

    @Schema(description = "Barcode", example = "194397215915")
    private String barcode;

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

    @Schema(description = "Preorder release date", example = "2024-12-25", type = "string", format = "date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate pre_order_release_date;

    @Schema(description = "Preorder end date", example = "2024-12-20", type = "string", format = "date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate pre_order_end_date;

    @Schema(description = "Type-specific details")
    private Object type_details;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Detailed artist information")
    public static class ArtistDetailDto {
        @Schema(description = "Artist ID", example = "1")
        private Integer id;

        @Schema(description = "Artist name", example = "The Beatles")
        private String name;

        @Schema(description = "Artist biography")
        private String biography;

        @Schema(description = "Formation date", example = "1960-08-01", type = "string", format = "date")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate formation_date;

        @Schema(description = "Career start date", example = "1960-08-01", type = "string", format = "date")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate career_start_date;

        @Schema(description = "Is this a band", example = "true")
        private Boolean is_band;

        @Schema(description = "Artist website", example = "https://www.thebeatles.com")
        private String website;
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

        @Schema(description = "Genre description")
        private String description;
    }
}