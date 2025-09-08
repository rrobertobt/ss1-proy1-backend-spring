package usac.cunoc.bpmn.dto.admin.reports;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for sales report response - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Sales report response data")
public class SalesReportResponseDto {

    @Schema(description = "Report period configuration")
    private PeriodDto period;

    @Schema(description = "Sales summary for the period")
    private SummaryDto summary;

    @Schema(description = "Sales breakdown by time period")
    private List<PeriodSalesDto> salesByPeriod;

    @Schema(description = "Sales breakdown by article type")
    private List<TypeSalesDto> salesByType;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Period configuration")
    public static class PeriodDto {
        @Schema(description = "Period start date", example = "2024-01-01")
        private LocalDate startDate;

        @Schema(description = "Period end date", example = "2024-01-31")
        private LocalDate endDate;

        @Schema(description = "Group by criteria", example = "daily")
        private String groupBy;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Sales summary")
    public static class SummaryDto {
        @Schema(description = "Total sales amount", example = "15000.50")
        private BigDecimal totalSales;

        @Schema(description = "Total number of orders", example = "125")
        private Integer totalOrders;

        @Schema(description = "Average order value", example = "120.00")
        private BigDecimal averageOrderValue;

        @Schema(description = "Total items sold", example = "250")
        private Integer totalItems;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Sales by period breakdown")
    public static class PeriodSalesDto {
        @Schema(description = "Period identifier", example = "2024-01-15")
        private String period;

        @Schema(description = "Total sales for period", example = "1250.75")
        private BigDecimal totalSales;

        @Schema(description = "Total orders for period", example = "10")
        private Integer totalOrders;

        @Schema(description = "Total items for period", example = "20")
        private Integer totalItems;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Sales by type breakdown")
    public static class TypeSalesDto {
        @Schema(description = "Article type", example = "vinyl")
        private String type;

        @Schema(description = "Total sales for type", example = "8500.25")
        private BigDecimal totalSales;

        @Schema(description = "Total orders for type", example = "75")
        private Integer totalOrders;

        @Schema(description = "Percentage of total sales", example = "56.67")
        private BigDecimal percentage;
    }
}