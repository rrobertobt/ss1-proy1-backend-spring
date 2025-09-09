package usac.cunoc.bpmn.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usac.cunoc.bpmn.dto.common.CurrencyDto;
import usac.cunoc.bpmn.dto.common.StatusDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for order summary information - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order summary information")
public class OrderSummaryDto {

    @Schema(description = "Order ID", example = "1")
    private Integer id;

    @Schema(description = "Order number", example = "ORD-2025-001234")
    private String orderNumber;

    @Schema(description = "Order status information")
    private StatusDto status;

    @Schema(description = "Total order amount", example = "125.50")
    private BigDecimal totalAmount;

    @Schema(description = "Currency information")
    private CurrencyDto currency;

    @Schema(description = "Total number of items", example = "3")
    private Integer totalItems;

    @Schema(description = "Order creation timestamp", example = "2025-09-08T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Order shipped timestamp", example = "2025-09-09T14:20:00")
    private LocalDateTime shippedAt;

    @Schema(description = "Order delivered timestamp", example = "2025-09-10T16:45:00")
    private LocalDateTime deliveredAt;
}