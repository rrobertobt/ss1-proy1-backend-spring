package usac.cunoc.bpmn.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

/**
 * Cart response DTO - Updated to match expected JSON structure
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Shopping cart with items")
public class CartResponseDto {

    @Schema(description = "Cart ID", example = "1")
    private Integer id;

    @Schema(description = "Total items count", example = "3")
    private Integer total_items;

    @Schema(description = "Cart subtotal", example = "89.97")
    private BigDecimal subtotal;

    @Schema(description = "Cart items")
    private List<CartItemDto> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Cart item details")
    public static class CartItemDto {

        @Schema(description = "Cart item ID", example = "15")
        private Integer id;

        @Schema(description = "Article information")
        private ArticleDto article;

        @Schema(description = "Quantity", example = "2")
        private Integer quantity;

        @Schema(description = "Unit price", example = "29.99")
        private BigDecimal unit_price;

        @Schema(description = "Discount applied", example = "0.00")
        private BigDecimal discount_applied;

        @Schema(description = "Total price for this item", example = "59.98")
        private BigDecimal total_price;

        @Schema(description = "Applied promotion", nullable = true)
        private PromotionDto promotion;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Article information with complete details")
    public static class ArticleDto {

        @Schema(description = "Article ID", example = "5")
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

        @Schema(description = "Article image URL", example = "https://example.com/image.jpg")
        private String image_url;

        @Schema(description = "Article availability", example = "true")
        private Boolean is_available;

        @Schema(description = "Is preorder item", example = "false")
        private Boolean is_preorder;

        @Schema(description = "Stock quantity", example = "15")
        private Integer stock_quantity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Artist information")
    public static class ArtistDto {

        @Schema(description = "Artist ID", example = "3")
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
    @Schema(description = "Promotion information")
    public static class PromotionDto {

        @Schema(description = "Promotion ID", example = "1")
        private Integer id;

        @Schema(description = "Promotion name", example = "Promoción Rock Clásico")
        private String name;

        @Schema(description = "Promotion type information")
        private promotion_typeDto promotion_type;

        @Schema(description = "Genre information", nullable = true)
        private GenreDto genre;

        @Schema(description = "Discount percentage", example = "10.00")
        private BigDecimal discount_percentage;

        @Schema(description = "Maximum items", example = "4")
        private Integer max_items;

        @Schema(description = "Start date", example = "2025-09-12T20:41:50.590Z")
        private String start_date;

        @Schema(description = "End date", example = "2025-09-12T20:41:50.590Z")
        private String end_date;

        @Schema(description = "Is promotion active", example = "true")
        private Boolean is_active;

        @Schema(description = "Usage count", example = "15")
        private Integer usage_count;

        @Schema(description = "Eligible articles for promotion")
        private List<EligibleArticleDto> eligible_articles;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Promotion type information")
    public static class promotion_typeDto {

        @Schema(description = "Promotion type ID", example = "1")
        private Integer id;

        @Schema(description = "Promotion type name", example = "Por Genero")
        private String name;
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

        @Schema(description = "Genre description", example = "string")
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Eligible article for promotion")
    public static class EligibleArticleDto {

        @Schema(description = "Article ID", example = "15")
        private Integer id;

        @Schema(description = "Article title", example = "Dark Side of the Moon")
        private String title;

        @Schema(description = "Artist name", example = "Pink Floyd")
        private String artist;

        @Schema(description = "Article price", example = "29.99")
        private BigDecimal price;

        @Schema(description = "Article image URL", example = "string")
        private String image_url;
    }
}