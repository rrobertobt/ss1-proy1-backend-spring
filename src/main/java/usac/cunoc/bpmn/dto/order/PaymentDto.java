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
 * DTO for payment information - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payment information")
public class PaymentDto {

    @Schema(description = "Payment ID", example = "1")
    private Integer id;

    @Schema(description = "Payment number", example = "PAY-2025-001234")
    private String payment_number;

    @Schema(description = "Payment method name", example = "Tarjeta de Credito")
    private String payment_method;

    @Schema(description = "Payment status information")
    private StatusDto status;

    @Schema(description = "Currency information")
    private CurrencyDto currency;

    @Schema(description = "Payment amount", example = "125.50")
    private BigDecimal amount;

    @Schema(description = "Transaction reference", example = "TXN123456789")
    private String transaction_reference;

    @Schema(description = "Gateway transaction ID", example = "GTW789123456")
    private String gateway_transaction_id;

    @Schema(description = "Refunded amount", example = "0.00")
    private BigDecimal refunded_amount;

    @Schema(description = "Payment processing timestamp", example = "2025-09-08T10:35:00")
    private LocalDateTime processed_at;

    @Schema(description = "Payment creation timestamp", example = "2025-09-08T10:30:00")
    private LocalDateTime created_at;
}