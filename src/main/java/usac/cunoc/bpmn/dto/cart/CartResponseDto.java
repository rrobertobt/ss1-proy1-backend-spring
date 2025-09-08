package usac.cunoc.bpmn.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

/**
 * Cart response DTO - matches PDF JSON structure (corrected from movements to
 * items)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Shopping cart with items")
public class CartResponseDto {

    @Schema(description = "Cart ID", example = "1")
    private Integer id;

    @Schema(description = "Total items count", example = "3")
    private Integer totalItems;

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
        private BigDecimal unitPrice;

        @Schema(description = "Discount applied", example = "0.00")
        private BigDecimal discountApplied;

        @Schema(description = "Total price for this item", example = "59.98")
        private BigDecimal totalPrice;

        @Schema(description = "Applied promotion", nullable = true)
        private PromotionDto promotion;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Article information")
    public static class ArticleDto {

        @Schema(description = "Article ID", example = "5")
        private Integer id;

        @Schema(description = "Article title", example = "Abbey Road")
        private String title;

        @Schema(description = "Artist name", example = "The Beatles")
        private String artist;

        @Schema(description = "Article image URL", example = "https://example.com/images/abbey-road.jpg")
        private String imageUrl;

        @Schema(description = "Article type", example = "vinyl", allowableValues = { "vinyl", "cassette", "cd" })
        private String type;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Promotion information")
    public static class PromotionDto {

        @Schema(description = "Promotion ID", example = "1")
        private Integer id;

        @Schema(description = "Promotion name", example = "Promoción Rock 10%")
        private String name;

        @Schema(description = "Discount percentage", example = "10.00")
        private BigDecimal discountPercentage;
    }
}