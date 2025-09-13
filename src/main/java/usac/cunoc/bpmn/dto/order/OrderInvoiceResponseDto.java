package usac.cunoc.bpmn.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usac.cunoc.bpmn.dto.common.CurrencyDto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for order invoice - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order invoice information")
public class OrderInvoiceResponseDto {

    @Schema(description = "Invoice ID", example = "1")
    private Integer id;

    @Schema(description = "Invoice number", example = "INV-2025-001234")
    private String invoice_number;

    @Schema(description = "Order ID", example = "1")
    private Integer order_id;

    @Schema(description = "Order number", example = "ORD-2025-001234")
    private String order_number;

    @Schema(description = "Currency information")
    private CurrencyDto currency;

    @Schema(description = "Issue date", example = "2025-09-08")
    private LocalDate issue_date;

    @Schema(description = "Due date", example = "2025-09-22")
    private LocalDate due_date;

    @Schema(description = "Customer tax ID", example = "12345678-K")
    private String tax_id;

    @Schema(description = "Subtotal amount", example = "100.00")
    private BigDecimal subtotal;

    @Schema(description = "Tax amount", example = "12.00")
    private BigDecimal tax_amount;

    @Schema(description = "Total amount", example = "112.00")
    private BigDecimal total_amount;

    @Schema(description = "Invoice notes", example = "Thank you for your purchase")
    private String notes;

    @Schema(description = "PDF download URL", example = "https://example.com/invoices/INV-2025-001234.pdf")
    private String pdf_Url;

    @Schema(description = "Invoice creation timestamp", example = "2025-09-08T10:30:00")
    private LocalDateTime created_at;
}