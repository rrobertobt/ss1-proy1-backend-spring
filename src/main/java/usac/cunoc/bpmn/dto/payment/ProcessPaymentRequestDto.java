package usac.cunoc.bpmn.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * DTO for payment processing request - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payment processing request data")
public class ProcessPaymentRequestDto {

    @NotNull(message = "Order ID is required")
    @Schema(description = "Order ID to process payment for", example = "1")
    private Integer order_id;

    @NotNull(message = "Payment method ID is required")
    @Schema(description = "Payment method ID", example = "1")
    private Integer payment_method_id;

    @NotNull(message = "Card ID is required")
    @Schema(description = "Credit card ID", example = "1")
    private Integer card_id;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Schema(description = "Payment amount", example = "150.75")
    private BigDecimal amount;
}