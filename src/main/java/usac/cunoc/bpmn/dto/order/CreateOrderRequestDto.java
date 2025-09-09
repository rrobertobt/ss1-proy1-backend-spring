package usac.cunoc.bpmn.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating orders - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for creating a new order")
public class CreateOrderRequestDto {

    @NotNull(message = "Shipping address ID is required")
    @Schema(description = "Shipping address ID", example = "1", required = true)
    private Integer shippingAddressId;

    @NotNull(message = "Billing address ID is required")
    @Schema(description = "Billing address ID", example = "1", required = true)
    private Integer billingAddressId;

    @NotNull(message = "Payment method ID is required")
    @Schema(description = "Payment method ID", example = "1", required = true)
    private Integer paymentMethodId;

    @Schema(description = "Credit card ID (if payment method requires card)", example = "1")
    private Integer cardId;

    @Schema(description = "Order notes", example = "Please deliver during business hours")
    private String notes;
}