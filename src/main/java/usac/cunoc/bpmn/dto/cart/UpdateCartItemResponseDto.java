package usac.cunoc.bpmn.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Response DTO for updating cart item quantity - matches PDF JSON structure
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response when updating cart item quantity")
public class UpdateCartItemResponseDto {

    @Schema(description = "Cart item ID", example = "15")
    private Integer item_id;

    @Schema(description = "New quantity", example = "3")
    private Integer quantity;

    @Schema(description = "Unit price", example = "29.99")
    private BigDecimal unit_price;

    @Schema(description = "Total price for this item", example = "89.97")
    private BigDecimal total_price;

    @Schema(description = "New cart total", example = "119.96")
    private BigDecimal new_cart_total;
}