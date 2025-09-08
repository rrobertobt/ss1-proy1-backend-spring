package usac.cunoc.bpmn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usac.cunoc.bpmn.dto.admin.reports.*;
import usac.cunoc.bpmn.repository.AdminReportsRepository;
import usac.cunoc.bpmn.service.AdminReportsService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Admin reports service implementation for generating business analytics
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminReportsServiceImpl implements AdminReportsService {

    private final AdminReportsRepository adminReportsRepository;

    @Override
    public SalesReportResponseDto getSalesReport(SalesReportRequestDto request) {
        log.info("Generating sales report from {} to {} grouped by {}",
                request.getStartDate(), request.getEndDate(), request.getGroupBy());

        validateDateRange(request.getStartDate(), request.getEndDate());
        validateGroupBy(request.getGroupBy());

        LocalDateTime startDateTime = request.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = request.getEndDate().atTime(23, 59, 59);

        try {
            // Get summary data
            Object[] summaryData = adminReportsRepository.getSalesReportSummary(startDateTime, endDateTime);
            SalesReportResponseDto.SummaryDto summary = buildSummary(summaryData);

            // Get period breakdown
            List<Object[]> periodData = adminReportsRepository.getSalesByPeriod(
                    startDateTime, endDateTime, request.getGroupBy());
            List<SalesReportResponseDto.PeriodSalesDto> salesByPeriod = buildPeriodSales(periodData);

            // Get type breakdown
            List<Object[]> typeData = adminReportsRepository.getSalesByType(startDateTime, endDateTime);
            List<SalesReportResponseDto.TypeSalesDto> salesByType = buildTypeSales(typeData);

            // Build period configuration
            SalesReportResponseDto.PeriodDto period = new SalesReportResponseDto.PeriodDto(
                    request.getStartDate(), request.getEndDate(), request.getGroupBy());

            return new SalesReportResponseDto(period, summary, salesByPeriod, salesByType);
        } catch (Exception e) {
            log.error("Error generating sales report: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar reporte de ventas: " + e.getMessage());
        }
    }

    @Override
    public TopArticlesResponseDto getTopArticles(TopArticlesRequestDto request) {
        log.info("Generating top articles report for {} days, limit: {}", request.getPeriod(), request.getLimit());

        validateLimit(request.getLimit());
        Integer periodDays = parsePeriodDays(request.getPeriod());

        try {
            List<Object[]> articlesData = adminReportsRepository.getTopSellingArticles(periodDays, request.getLimit());

            List<TopArticlesResponseDto.TopArticleDto> topArticles = new ArrayList<>();
            for (int i = 0; i < articlesData.size(); i++) {
                Object[] row = articlesData.get(i);
                TopArticlesResponseDto.TopArticleDto article = buildTopArticle(row, i + 1);
                if (article != null) {
                    topArticles.add(article);
                }
            }

            String periodDescription = buildPeriodDescription(request.getPeriod());
            return new TopArticlesResponseDto(periodDescription, topArticles);
        } catch (Exception e) {
            log.error("Error generating top articles report: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar reporte de artículos más vendidos: " + e.getMessage());
        }
    }

    @Override
    public TopCustomersResponseDto getTopCustomers(TopCustomersRequestDto request) {
        log.info("Generating top customers report, limit: {}", request.getLimit());

        validateLimit(request.getLimit());

        try {
            List<Object[]> customersData = adminReportsRepository.getTopCustomers(request.getLimit());

            List<TopCustomersResponseDto.TopCustomerDto> topCustomers = new ArrayList<>();
            for (int i = 0; i < customersData.size(); i++) {
                Object[] row = customersData.get(i);
                TopCustomersResponseDto.TopCustomerDto customer = buildTopCustomer(row, i + 1);
                if (customer != null) {
                    topCustomers.add(customer);
                }
            }

            String periodDescription = buildPeriodDescription(request.getPeriod());
            return new TopCustomersResponseDto(periodDescription, topCustomers);
        } catch (Exception e) {
            log.error("Error generating top customers report: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar reporte de mejores clientes: " + e.getMessage());
        }
    }

    @Override
    public TopRatedResponseDto getTopRatedArticles(TopRatedRequestDto request) {
        log.info("Generating top rated articles report with min ratings: {}, limit: {}",
                request.getMinRatings(), request.getLimit());

        validateMinRatings(request.getMinRatings());
        validateLimit(request.getLimit());

        try {
            List<Object[]> articlesData = adminReportsRepository.getTopRatedArticles(
                    request.getMinRatings(), request.getLimit());

            List<TopRatedResponseDto.TopRatedArticleDto> topRatedArticles = new ArrayList<>();
            for (int i = 0; i < articlesData.size(); i++) {
                Object[] row = articlesData.get(i);
                TopRatedResponseDto.TopRatedArticleDto article = buildTopRatedArticle(row, i + 1);
                if (article != null) {
                    topRatedArticles.add(article);
                }
            }

            TopRatedResponseDto.CriteriaDto criteria = new TopRatedResponseDto.CriteriaDto(
                    request.getMinRatings(), request.getLimit());

            return new TopRatedResponseDto(criteria, topRatedArticles);
        } catch (Exception e) {
            log.error("Error generating top rated articles report: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar reporte de artículos mejor calificados: " + e.getMessage());
        }
    }

    @Override
    public InventoryResponseDto getInventoryReport() {
        log.info("Generating inventory report");

        try {
            // Get inventory summary
            Object[] summaryData = adminReportsRepository.getInventorySummary();
            InventoryResponseDto.SummaryDto summary = buildInventorySummary(summaryData);

            // Get by type breakdown
            List<Object[]> typeData = adminReportsRepository.getInventoryByType();
            List<InventoryResponseDto.TypeInventoryDto> byType = buildInventoryByType(typeData);

            // Get by genre breakdown
            List<Object[]> genreData = adminReportsRepository.getInventoryByGenre();
            List<InventoryResponseDto.GenreInventoryDto> byGenre = buildInventoryByGenre(genreData);

            return new InventoryResponseDto(summary, byType, byGenre);
        } catch (Exception e) {
            log.error("Error generating inventory report: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar reporte de inventario: " + e.getMessage());
        }
    }

    @Override
    public StockAlertsResponseDto getStockAlerts() {
        log.info("Generating stock alerts report");

        try {
            List<Object[]> alertsData = adminReportsRepository.getStockAlerts();

            List<StockAlertsResponseDto.StockAlertDto> alerts = new ArrayList<>();

            for (Object[] row : alertsData) {
                StockAlertsResponseDto.StockAlertDto alert = buildStockAlert(row);
                if (alert != null) {
                    alerts.add(alert);
                }
            }

            return new StockAlertsResponseDto(alerts);
        } catch (Exception e) {
            log.error("Error generating stock alerts report: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar reporte de alertas de stock: " + e.getMessage());
        }
    }

    // Validation methods
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son requeridas");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        if (startDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser futura");
        }
        if (startDate.isBefore(LocalDate.now().minusYears(2))) {
            throw new IllegalArgumentException("El rango de fechas no puede exceder 2 años");
        }
    }

    private void validateGroupBy(String groupBy) {
        if (groupBy == null || groupBy.trim().isEmpty()) {
            throw new IllegalArgumentException("El criterio de agrupación es requerido");
        }
        if (!List.of("daily", "weekly", "monthly").contains(groupBy.toLowerCase())) {
            throw new IllegalArgumentException("Criterio de agrupación inválido. Debe ser: daily, weekly o monthly");
        }
    }

    private void validateLimit(Integer limit) {
        if (limit == null || limit < 1) {
            throw new IllegalArgumentException("El límite debe ser mayor a 0");
        }
        if (limit > 50) {
            throw new IllegalArgumentException("El límite no puede exceder 50");
        }
    }

    private void validateMinRatings(Integer minRatings) {
        if (minRatings == null || minRatings < 1) {
            throw new IllegalArgumentException("El mínimo de calificaciones debe ser mayor a 0");
        }
        if (minRatings > 100) {
            throw new IllegalArgumentException("El mínimo de calificaciones no puede exceder 100");
        }
    }

    // Helper methods for building DTOs
    private SalesReportResponseDto.SummaryDto buildSummary(Object[] data) {
        if (data == null || data.length < 4) {
            return new SalesReportResponseDto.SummaryDto(
                    BigDecimal.ZERO, 0, BigDecimal.ZERO, 0);
        }

        BigDecimal totalSales = data[0] != null ? new BigDecimal(data[0].toString()) : BigDecimal.ZERO;
        Integer totalOrders = data[1] != null ? ((Number) data[1]).intValue() : 0;
        BigDecimal averageOrderValue = data[2] != null ? new BigDecimal(data[2].toString()) : BigDecimal.ZERO;
        Integer totalItems = data[3] != null ? ((Number) data[3]).intValue() : 0;

        return new SalesReportResponseDto.SummaryDto(
                totalSales.setScale(2, RoundingMode.HALF_UP),
                totalOrders,
                averageOrderValue.setScale(2, RoundingMode.HALF_UP),
                totalItems);
    }

    private List<SalesReportResponseDto.PeriodSalesDto> buildPeriodSales(List<Object[]> data) {
        List<SalesReportResponseDto.PeriodSalesDto> result = new ArrayList<>();

        if (data == null || data.isEmpty()) {
            return result;
        }

        for (Object[] row : data) {
            if (row == null || row.length < 4)
                continue;

            String period = row[0] != null ? row[0].toString() : "";
            BigDecimal totalSales = row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;
            Integer totalOrders = row[2] != null ? ((Number) row[2]).intValue() : 0;
            Integer totalItems = row[3] != null ? ((Number) row[3]).intValue() : 0;

            result.add(new SalesReportResponseDto.PeriodSalesDto(
                    period,
                    totalSales.setScale(2, RoundingMode.HALF_UP),
                    totalOrders,
                    totalItems));
        }

        return result;
    }

    private List<SalesReportResponseDto.TypeSalesDto> buildTypeSales(List<Object[]> data) {
        List<SalesReportResponseDto.TypeSalesDto> result = new ArrayList<>();

        if (data == null || data.isEmpty()) {
            return result;
        }

        for (Object[] row : data) {
            if (row == null || row.length < 4)
                continue;

            String type = row[0] != null ? row[0].toString() : "unknown";
            BigDecimal totalSales = row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;
            Integer totalOrders = row[2] != null ? ((Number) row[2]).intValue() : 0;
            BigDecimal percentage = row[3] != null ? new BigDecimal(row[3].toString()) : BigDecimal.ZERO;

            result.add(new SalesReportResponseDto.TypeSalesDto(
                    type,
                    totalSales.setScale(2, RoundingMode.HALF_UP),
                    totalOrders,
                    percentage.setScale(2, RoundingMode.HALF_UP)));
        }

        return result;
    }

    private TopArticlesResponseDto.TopArticleDto buildTopArticle(Object[] row, int rank) {
        if (row == null || row.length < 8) {
            return null;
        }

        Integer id = row[0] != null ? ((Number) row[0]).intValue() : 0;
        String title = row[1] != null ? row[1].toString() : "";
        String artistName = row[2] != null ? row[2].toString() : "";
        String type = row[3] != null ? row[3].toString() : "unknown";
        String imageUrl = row[4] != null ? row[4].toString() : "";
        Integer totalSold = row[5] != null ? ((Number) row[5]).intValue() : 0;
        BigDecimal totalRevenue = row[6] != null ? new BigDecimal(row[6].toString()) : BigDecimal.ZERO;
        BigDecimal averageRating = row[7] != null ? new BigDecimal(row[7].toString()) : BigDecimal.ZERO;

        TopArticlesResponseDto.ArticleDto article = new TopArticlesResponseDto.ArticleDto(
                id, title, artistName, type, imageUrl);

        return new TopArticlesResponseDto.TopArticleDto(
                rank,
                article,
                totalSold,
                totalRevenue.setScale(2, RoundingMode.HALF_UP),
                averageRating.setScale(2, RoundingMode.HALF_UP));
    }

    private TopCustomersResponseDto.TopCustomerDto buildTopCustomer(Object[] row, int rank) {
        if (row == null || row.length < 9) {
            return null;
        }

        Integer id = row[0] != null ? ((Number) row[0]).intValue() : 0;
        String username = row[1] != null ? row[1].toString() : "";
        String firstName = row[2] != null ? row[2].toString() : "";
        String lastName = row[3] != null ? row[3].toString() : "";
        String email = row[4] != null ? row[4].toString() : "";
        BigDecimal totalSpent = row[5] != null ? new BigDecimal(row[5].toString()) : BigDecimal.ZERO;
        Integer totalOrders = row[6] != null ? ((Number) row[6]).intValue() : 0;
        BigDecimal averageOrderValue = row[7] != null ? new BigDecimal(row[7].toString()) : BigDecimal.ZERO;
        Timestamp lastOrderTimestamp = (Timestamp) row[8];

        TopCustomersResponseDto.CustomerDto customer = new TopCustomersResponseDto.CustomerDto(
                id, username, firstName, lastName, email);

        LocalDateTime lastOrderDate = lastOrderTimestamp != null ? lastOrderTimestamp.toLocalDateTime() : null;

        return new TopCustomersResponseDto.TopCustomerDto(
                rank,
                customer,
                totalSpent.setScale(2, RoundingMode.HALF_UP),
                totalOrders,
                averageOrderValue.setScale(2, RoundingMode.HALF_UP),
                lastOrderDate);
    }

    private TopRatedResponseDto.TopRatedArticleDto buildTopRatedArticle(Object[] row, int rank) {
        if (row == null || row.length < 8) {
            return null;
        }

        Integer id = row[0] != null ? ((Number) row[0]).intValue() : 0;
        String title = row[1] != null ? row[1].toString() : "";
        String artistName = row[2] != null ? row[2].toString() : "";
        String type = row[3] != null ? row[3].toString() : "unknown";
        String imageUrl = row[4] != null ? row[4].toString() : "";
        BigDecimal averageRating = row[5] != null ? new BigDecimal(row[5].toString()) : BigDecimal.ZERO;
        Integer totalRatings = row[6] != null ? ((Number) row[6]).intValue() : 0;
        Integer totalSold = row[7] != null ? ((Number) row[7]).intValue() : 0;

        TopRatedResponseDto.ArticleDto article = new TopRatedResponseDto.ArticleDto(
                id, title, artistName, type, imageUrl);

        return new TopRatedResponseDto.TopRatedArticleDto(
                rank,
                article,
                averageRating.setScale(2, RoundingMode.HALF_UP),
                totalRatings,
                totalSold);
    }

    private InventoryResponseDto.SummaryDto buildInventorySummary(Object[] data) {
        if (data == null || data.length < 5) {
            return new InventoryResponseDto.SummaryDto(
                    0, 0, 0, 0, BigDecimal.ZERO);
        }

        Integer totalArticles = data[0] != null ? ((Number) data[0]).intValue() : 0;
        Integer totalStock = data[1] != null ? ((Number) data[1]).intValue() : 0;
        Integer lowStockArticles = data[2] != null ? ((Number) data[2]).intValue() : 0;
        Integer outOfStockArticles = data[3] != null ? ((Number) data[3]).intValue() : 0;
        BigDecimal totalInventoryValue = data[4] != null ? new BigDecimal(data[4].toString()) : BigDecimal.ZERO;

        return new InventoryResponseDto.SummaryDto(
                totalArticles,
                totalStock,
                lowStockArticles,
                outOfStockArticles,
                totalInventoryValue.setScale(2, RoundingMode.HALF_UP));
    }

    private List<InventoryResponseDto.TypeInventoryDto> buildInventoryByType(List<Object[]> data) {
        List<InventoryResponseDto.TypeInventoryDto> result = new ArrayList<>();

        if (data == null || data.isEmpty()) {
            return result;
        }

        for (Object[] row : data) {
            if (row == null || row.length < 4)
                continue;

            String type = row[0] != null ? row[0].toString() : "unknown";
            Integer totalArticles = row[1] != null ? ((Number) row[1]).intValue() : 0;
            Integer totalStock = row[2] != null ? ((Number) row[2]).intValue() : 0;
            BigDecimal inventoryValue = row[3] != null ? new BigDecimal(row[3].toString()) : BigDecimal.ZERO;

            result.add(new InventoryResponseDto.TypeInventoryDto(
                    type,
                    totalArticles,
                    totalStock,
                    inventoryValue.setScale(2, RoundingMode.HALF_UP)));
        }

        return result;
    }

    private List<InventoryResponseDto.GenreInventoryDto> buildInventoryByGenre(List<Object[]> data) {
        List<InventoryResponseDto.GenreInventoryDto> result = new ArrayList<>();

        if (data == null || data.isEmpty()) {
            return result;
        }

        for (Object[] row : data) {
            if (row == null || row.length < 3)
                continue;

            String genre = row[0] != null ? row[0].toString() : "";
            Integer totalArticles = row[1] != null ? ((Number) row[1]).intValue() : 0;
            Integer totalStock = row[2] != null ? ((Number) row[2]).intValue() : 0;

            result.add(new InventoryResponseDto.GenreInventoryDto(
                    genre,
                    totalArticles,
                    totalStock));
        }

        return result;
    }

    private StockAlertsResponseDto.StockAlertDto buildStockAlert(Object[] row) {
        if (row == null || row.length < 8) {
            return null;
        }

        Integer articleId = row[0] != null ? ((Number) row[0]).intValue() : 0;
        String title = row[1] != null ? row[1].toString() : "";
        String artistName = row[2] != null ? row[2].toString() : "";
        String type = row[3] != null ? row[3].toString() : "unknown";
        Integer currentStock = row[4] != null ? ((Number) row[4]).intValue() : 0;
        Integer minStockLevel = row[5] != null ? ((Number) row[5]).intValue() : 0;
        String imageUrl = row[6] != null ? row[6].toString() : "";
        String severity = row[7] != null ? row[7].toString() : "normal";

        return new StockAlertsResponseDto.StockAlertDto(
                articleId,
                title,
                artistName,
                type,
                currentStock,
                minStockLevel,
                imageUrl,
                severity);
    }

    private Integer parsePeriodDays(String period) {
        if (period == null || period.trim().isEmpty()) {
            return 30;
        }

        switch (period.trim()) {
            case "7":
                return 7;
            case "30":
                return 30;
            case "90":
                return 90;
            case "365":
                return 365;
            default:
                log.warn("Invalid period: {}, defaulting to 30 days", period);
                return 30;
        }
    }

    private String buildPeriodDescription(String period) {
        if (period == null || period.trim().isEmpty()) {
            return "30 days";
        }

        switch (period.trim()) {
            case "7":
                return "7 days";
            case "30":
                return "30 days";
            case "90":
                return "90 days";
            case "365":
                return "365 days";
            default:
                return "30 days";
        }
    }
}