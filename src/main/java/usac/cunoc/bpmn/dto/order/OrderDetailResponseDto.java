package usac.cunoc.bpmn.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usac.cunoc.bpmn.dto.common.CurrencyDto;
import usac.cunoc.bpmn.dto.common.StatusDto;
import usac.cunoc.bpmn.dto.user.UserAddressDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for order details - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detailed order information")
public class OrderDetailResponseDto {

    @Schema(description = "Order ID", example = "1")
    private Integer id;

    @Schema(description = "Order number", example = "ORD-2025-001234")
    private String orderNumber;

    @Schema(description = "Order status information")
    private StatusDto status;

    @Schema(description = "Currency information")
    private CurrencyDto currency;

    @Schema(description = "Subtotal amount", example = "100.00")
    private BigDecimal subtotal;

    @Schema(description = "Tax amount", example = "12.00")
    private BigDecimal taxAmount;

    @Schema(description = "Discount amount", example = "5.00")
    private BigDecimal discountAmount;

    @Schema(description = "Shipping cost", example = "18.50")
    private BigDecimal shippingCost;

    @Schema(description = "Total order amount", example = "125.50")
    private BigDecimal totalAmount;

    @Schema(description = "Total number of items", example = "3")
    private Integer totalItems;

    @Schema(description = "Shipping address information")
    private UserAddressDto shippingAddress;

    @Schema(description = "Billing address information")
    private UserAddressDto billingAddress;

    @Schema(description = "Order notes", example = "Please deliver during business hours")
    private String notes;

    @Schema(description = "Order items")
    private List<OrderItemDto> items;

    @Schema(description = "Payment information")
    private List<PaymentDto> payments;

    @Schema(description = "Order creation timestamp", example = "2025-09-08T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Order shipped timestamp", example = "2025-09-09T14:20:00")
    private LocalDateTime shippedAt;

    @Schema(description = "Order delivered timestamp", example = "2025-09-10T16:45:00")
    private LocalDateTime deliveredAt;

    @Schema(description = "Last update timestamp", example = "2025-09-08T11:15:00")
    private LocalDateTime updatedAt;
}