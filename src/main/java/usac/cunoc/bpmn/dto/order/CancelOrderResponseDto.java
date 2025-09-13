package usac.cunoc.bpmn.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Response DTO for canceling orders - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response when canceling an order")
public class CancelOrderResponseDto {

    @Schema(description = "Order ID", example = "1")
    private Integer order_id;

    @Schema(description = "Order number", example = "ORD-2025-001234")
    private String order_number;

    @Schema(description = "New order status", example = "Cancelado")
    private String status;

    @Schema(description = "Cancellation timestamp", example = "2025-09-08T15:30:00")
    private LocalDateTime cancelled_at;
}