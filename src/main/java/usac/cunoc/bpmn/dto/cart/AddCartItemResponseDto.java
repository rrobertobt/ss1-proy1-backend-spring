package usac.cunoc.bpmn.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Response DTO for adding item to cart - matches PDF JSON structure
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response when adding item to cart")
public class AddCartItemResponseDto {

    @Schema(description = "Cart ID", example = "1")
    private Integer cartId;

    @Schema(description = "Cart item ID", example = "15")
    private Integer itemId;

    @Schema(description = "Article ID", example = "5")
    private Integer articleId;

    @Schema(description = "Quantity added", example = "2")
    private Integer quantity;

    @Schema(description = "Unit price", example = "29.99")
    private BigDecimal unitPrice;

    @Schema(description = "Total price for this item", example = "59.98")
    private BigDecimal totalPrice;

    @Schema(description = "New cart total", example = "89.97")
    private BigDecimal newCartTotal;
}