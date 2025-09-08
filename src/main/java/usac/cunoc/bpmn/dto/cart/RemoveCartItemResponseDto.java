package usac.cunoc.bpmn.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Response DTO for removing item from cart - matches PDF JSON structure
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response when removing item from cart")
public class RemoveCartItemResponseDto {

    @Schema(description = "Removed item ID", example = "15")
    private Integer removedItemId;

    @Schema(description = "New cart total", example = "29.99")
    private BigDecimal newCartTotal;

    @Schema(description = "New item count", example = "1")
    private Integer newItemCount;
}