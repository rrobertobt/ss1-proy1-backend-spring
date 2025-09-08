package usac.cunoc.bpmn.dto.admin.reports;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for top customers report response - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Top customers report response data")
public class TopCustomersResponseDto {

    @Schema(description = "Report period", example = "30 days")
    private String period;

    @Schema(description = "List of top customers")
    private List<TopCustomerDto> topCustomers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Top customer information")
    public static class TopCustomerDto {
        @Schema(description = "Customer rank", example = "1")
        private Integer rank;

        @Schema(description = "Customer details")
        private CustomerDto customer;

        @Schema(description = "Total amount spent", example = "1250.75")
        private BigDecimal totalSpent;

        @Schema(description = "Total number of orders", example = "15")
        private Integer totalOrders;

        @Schema(description = "Average order value", example = "83.38")
        private BigDecimal averageOrderValue;

        @Schema(description = "Last order date", example = "2024-01-15T10:30:00")
        private LocalDateTime lastOrderDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Customer basic information")
    public static class CustomerDto {
        @Schema(description = "Customer ID", example = "1")
        private Integer id;

        @Schema(description = "Username", example = "johndoe123")
        private String username;

        @Schema(description = "First name", example = "John")
        private String firstName;

        @Schema(description = "Last name", example = "Doe")
        private String lastName;

        @Schema(description = "Email address", example = "john.doe@example.com")
        private String email;
    }
}