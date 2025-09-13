package usac.cunoc.bpmn.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usac.cunoc.bpmn.dto.common.CurrencyDto;
import usac.cunoc.bpmn.dto.order.OrderItemDto;
import usac.cunoc.bpmn.dto.user.UserAddressDto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for payment processing response - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payment processing response data")
public class ProcessPaymentResponseDto {

    @Schema(description = "Invoice number", example = "INV-20250908142530-ABC123")
    private String invoice_number;

    @Schema(description = "Order ID", example = "1")
    private Integer order_id;

    @Schema(description = "Order number", example = "ORD-20250908142530-DEF456")
    private String order_number;

    @Schema(description = "Invoice issue date")
    private LocalDate issue_date;

    @Schema(description = "Invoice due date")
    private LocalDate due_date;

    @Schema(description = "Subtotal amount", example = "125.50")
    private BigDecimal subtotal;

    @Schema(description = "Tax amount", example = "15.06")
    private BigDecimal tax_amount;

    @Schema(description = "Total amount", example = "140.56")
    private BigDecimal total_amount;

    @Schema(description = "Currency information")
    private CurrencyDto currency;

    @Schema(description = "PDF download URL", example = "/api/v1/invoices/1/pdf")
    private String pdf_url;

    @Schema(description = "Customer information")
    private CustomerDto customer;

    @Schema(description = "Billing address")
    private UserAddressDto billing_address;

    @Schema(description = "Order items")
    private List<OrderItemDto> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Customer information")
    public static class CustomerDto {
        @Schema(description = "Customer first name", example = "John")
        private String first_name;

        @Schema(description = "Customer last name", example = "Doe")
        private String last_name;

        @Schema(description = "Customer email", example = "john.doe@example.com")
        private String email;
    }
}