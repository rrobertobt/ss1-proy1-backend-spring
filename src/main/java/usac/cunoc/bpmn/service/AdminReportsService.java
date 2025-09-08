package usac.cunoc.bpmn.service;

import usac.cunoc.bpmn.dto.admin.reports.*;

/**
 * Admin reports service interface for generating business reports
 */
public interface AdminReportsService {

    /**
     * Generate sales report by period
     */
    SalesReportResponseDto getSalesReport(SalesReportRequestDto request);

    /**
     * Get top selling articles report
     */
    TopArticlesResponseDto getTopArticles(TopArticlesRequestDto request);

    /**
     * Get top customers by spending report
     */
    TopCustomersResponseDto getTopCustomers(TopCustomersRequestDto request);

    /**
     * Get top rated articles report
     */
    TopRatedResponseDto getTopRatedArticles(TopRatedRequestDto request);

    /**
     * Get inventory status report
     */
    InventoryResponseDto getInventoryReport();

    /**
     * Get stock alerts report
     */
    StockAlertsResponseDto getStockAlerts();
}