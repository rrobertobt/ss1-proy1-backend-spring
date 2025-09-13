package usac.cunoc.bpmn.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO for applying CD promotion - matches PDF JSON structure
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response when applying CD promotion")
public class ApplyPromotionResponseDto {

    @Schema(description = "Promotion ID", example = "1")
    private Integer promotion_id;

    @Schema(description = "List of item IDs that had promotion applied", example = "[15, 16, 17]")
    private List<Integer> applied_to_items;

    @Schema(description = "Total discount amount applied", example = "8.99")
    private BigDecimal total_discount;

    @Schema(description = "New cart total after promotion", example = "80.98")
    private BigDecimal new_cart_total;
}