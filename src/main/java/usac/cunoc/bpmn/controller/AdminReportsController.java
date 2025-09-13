package usac.cunoc.bpmn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import usac.cunoc.bpmn.dto.admin.reports.*;
import usac.cunoc.bpmn.dto.common.ApiResponseDto;
import usac.cunoc.bpmn.service.AdminReportsService;
import java.time.LocalDate;

/**
 * Admin reports controller for generating business analytics and reports
 */
@RestController
@RequestMapping("/api/v1/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Reports", description = "Administrative reports and analytics operations")
public class AdminReportsController {

        private final AdminReportsService adminReportsService;

        @GetMapping("/sales")
        @Operation(summary = "Generate sales report", description = "Generate comprehensive sales report for specified period with breakdown by time and type")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Sales report generated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
        })
        public ResponseEntity<ApiResponseDto<SalesReportResponseDto>> getSalesReport(
                        @Parameter(description = "Report start date", example = "2024-01-01") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

                        @Parameter(description = "Report end date", example = "2024-01-31") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

                        @Parameter(description = "Group by criteria", example = "daily") @RequestParam(defaultValue = "daily") String groupBy) {

                SalesReportRequestDto request = new SalesReportRequestDto();
                request.setStart_date(startDate);
                request.setEnd_date(endDate);
                request.setGroup_by(groupBy);

                SalesReportResponseDto response = adminReportsService.getSalesReport(request);
                return ResponseEntity.ok(ApiResponseDto.success("Reporte de ventas generado exitosamente", response));
        }

        @GetMapping("/top-articles")
        @Operation(summary = "Get top selling articles", description = "Get ranking of best selling articles for specified period")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Top articles report generated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
        })
        public ResponseEntity<ApiResponseDto<TopArticlesResponseDto>> getTopArticles(
                        @Parameter(description = "Period in days", example = "30") @RequestParam(defaultValue = "30") String period,

                        @Parameter(description = "Maximum number of articles to return", example = "10") @RequestParam(defaultValue = "10") Integer limit) {

                TopArticlesRequestDto request = new TopArticlesRequestDto();
                request.setPeriod(period);
                request.setLimit(limit);

                TopArticlesResponseDto response = adminReportsService.getTopArticles(request);
                return ResponseEntity.ok(ApiResponseDto
                                .success("Reporte de artículos más vendidos generado exitosamente", response));
        }

        @GetMapping("/top-customers")
        @Operation(summary = "Get top customers by spending", description = "Get ranking of customers with highest spending")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Top customers report generated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
        })
        public ResponseEntity<ApiResponseDto<TopCustomersResponseDto>> getTopCustomers(
                        @Parameter(description = "Period in days", example = "30") @RequestParam(defaultValue = "30") String period,

                        @Parameter(description = "Maximum number of customers to return", example = "10") @RequestParam(defaultValue = "10") Integer limit) {

                TopCustomersRequestDto request = new TopCustomersRequestDto();
                request.setPeriod(period);
                request.setLimit(limit);

                TopCustomersResponseDto response = adminReportsService.getTopCustomers(request);
                return ResponseEntity.ok(
                                ApiResponseDto.success("Reporte de mejores clientes generado exitosamente", response));
        }

        @GetMapping("/top-rated")
        @Operation(summary = "Get top rated articles", description = "Get ranking of articles with highest average ratings")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Top rated articles report generated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
        })
        public ResponseEntity<ApiResponseDto<TopRatedResponseDto>> getTopRatedArticles(
                        @Parameter(description = "Minimum number of ratings required", example = "5") @RequestParam(defaultValue = "5") Integer minRatings,

                        @Parameter(description = "Maximum number of articles to return", example = "10") @RequestParam(defaultValue = "10") Integer limit) {

                TopRatedRequestDto request = new TopRatedRequestDto();
                request.setMin_ratings(minRatings);
                request.setLimit(limit);

                TopRatedResponseDto response = adminReportsService.getTopRatedArticles(request);
                return ResponseEntity.ok(ApiResponseDto
                                .success("Reporte de artículos mejor calificados generado exitosamente", response));
        }

        @GetMapping("/inventory")
        @Operation(summary = "Get inventory report", description = "Get comprehensive inventory status report with breakdown by type and genre")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Inventory report generated successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
        })
        public ResponseEntity<ApiResponseDto<InventoryResponseDto>> getInventoryReport() {
                InventoryResponseDto response = adminReportsService.getInventoryReport();
                return ResponseEntity
                                .ok(ApiResponseDto.success("Reporte de inventario generado exitosamente", response));
        }

        @GetMapping("/stock-alerts")
        @Operation(summary = "Get stock alerts", description = "Get list of articles with low stock or out of stock alerts")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Stock alerts report generated successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
        })
        public ResponseEntity<ApiResponseDto<StockAlertsResponseDto>> getStockAlerts() {
                StockAlertsResponseDto response = adminReportsService.getStockAlerts();
                return ResponseEntity.ok(
                                ApiResponseDto.success("Reporte de alertas de stock generado exitosamente", response));
        }
}