package usac.cunoc.bpmn.dto.wishlist;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for preorder payment from wishlist
 * Matches PDF specification exactly for POST
 * /api/v1/wishlist/items/{id}/preorder-payment response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response after successful preorder payment from wishlist")
public class WishlistPreorderPaymentResponseDto {

    @Schema(description = "Payment ID", example = "25")
    private Integer payment_id;

    @Schema(description = "Wishlist item ID that was paid", example = "10")
    private Integer wishlist_item_id;

    @Schema(description = "Article ID", example = "5")
    private Integer article_id;

    @Schema(description = "Payment amount", example = "29.99")
    private BigDecimal amount;

    @Schema(description = "Preorder payment status", example = "true")
    private Boolean is_preorder_paid;

    @Schema(description = "Payment processing timestamp", example = "2024-03-15T10:35:00")
    private LocalDateTime processed_at;
}