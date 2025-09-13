package usac.cunoc.bpmn.dto.wishlist;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for preorder payment from wishlist
 * Matches PDF specification exactly for POST
 * /api/v1/wishlist/items/{id}/preorder-payment
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to pay for preorder from wishlist")
public class WishlistPreorderPaymentRequestDto {

    @NotNull(message = "payment_method_id es requerido")
    @Positive(message = "payment_method_id debe ser positivo")
    @Schema(description = "Payment method ID", example = "1", required = true)
    private Integer payment_method_id;

    @Schema(description = "Card ID if payment method requires card", example = "5")
    private Integer card_id;
}