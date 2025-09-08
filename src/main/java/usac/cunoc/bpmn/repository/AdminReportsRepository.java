package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.AnalogArticle;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for admin reports operations with native SQL queries
 */
@Repository
public interface AdminReportsRepository extends JpaRepository<AnalogArticle, Integer> {

    /**
     * Get sales report summary for a period
     */
    @Query(value = """
            SELECT
                COALESCE(SUM(o.total_amount), 0) as totalSales,
                COALESCE(COUNT(DISTINCT o.id), 0) as totalOrders,
                CASE
                    WHEN COUNT(DISTINCT o.id) > 0 THEN COALESCE(SUM(o.total_amount), 0) / COUNT(DISTINCT o.id)
                    ELSE 0
                END as averageOrderValue,
                COALESCE(SUM(oi.quantity), 0) as totalItems
            FROM "order" o
            LEFT JOIN order_item oi ON o.id = oi.order_id
            WHERE o.order_status_id = 4
            AND o.created_at BETWEEN :startDate AND :endDate
            """, nativeQuery = true)
    Object[] getSalesReportSummary(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Get sales by period (daily, weekly, monthly)
     */
    @Query(value = """
            SELECT
                CASE
                    WHEN :groupBy = 'daily' THEN TO_CHAR(o.created_at, 'YYYY-MM-DD')
                    WHEN :groupBy = 'weekly' THEN TO_CHAR(DATE_TRUNC('week', o.created_at), 'YYYY-MM-DD')
                    WHEN :groupBy = 'monthly' THEN TO_CHAR(DATE_TRUNC('month', o.created_at), 'YYYY-MM-DD')
                    ELSE TO_CHAR(o.created_at, 'YYYY-MM-DD')
                END as period,
                COALESCE(SUM(o.total_amount), 0) as totalSales,
                COALESCE(COUNT(DISTINCT o.id), 0) as totalOrders,
                COALESCE(SUM(oi.quantity), 0) as totalItems
            FROM "order" o
            LEFT JOIN order_item oi ON o.id = oi.order_id
            WHERE o.order_status_id = 4
            AND o.created_at BETWEEN :startDate AND :endDate
            GROUP BY
                CASE
                    WHEN :groupBy = 'daily' THEN TO_CHAR(o.created_at, 'YYYY-MM-DD')
                    WHEN :groupBy = 'weekly' THEN TO_CHAR(DATE_TRUNC('week', o.created_at), 'YYYY-MM-DD')
                    WHEN :groupBy = 'monthly' THEN TO_CHAR(DATE_TRUNC('month', o.created_at), 'YYYY-MM-DD')
                    ELSE TO_CHAR(o.created_at, 'YYYY-MM-DD')
                END
            ORDER BY period
            """, nativeQuery = true)
    List<Object[]> getSalesByPeriod(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("groupBy") String groupBy);

    /**
     * Get sales by article type
     */
    @Query(value = """
            SELECT
                CASE
                    WHEN v.id IS NOT NULL THEN 'vinyl'
                    WHEN c.id IS NOT NULL THEN 'cassette'
                    WHEN cd.id IS NOT NULL THEN 'cd'
                    ELSE 'unknown'
                END as type,
                COALESCE(SUM(oi.total_price), 0) as totalSales,
                COALESCE(COUNT(DISTINCT o.id), 0) as totalOrders,
                ROUND(
                    CASE
                        WHEN (SELECT SUM(o2.total_amount) FROM "order" o2 WHERE o2.order_status_id = 4
                              AND o2.created_at BETWEEN :startDate AND :endDate) > 0
                        THEN (COALESCE(SUM(oi.total_price), 0) * 100.0 /
                              (SELECT SUM(o2.total_amount) FROM "order" o2 WHERE o2.order_status_id = 4
                               AND o2.created_at BETWEEN :startDate AND :endDate))
                        ELSE 0
                    END, 2
                ) as percentage
            FROM "order" o
            JOIN order_item oi ON o.id = oi.order_id
            JOIN analog_article aa ON oi.analog_article_id = aa.id
            LEFT JOIN vinyl v ON aa.id = v.analog_article_id
            LEFT JOIN cassette c ON aa.id = c.analog_article_id
            LEFT JOIN cd ON aa.id = cd.analog_article_id
            WHERE o.order_status_id = 4
            AND o.created_at BETWEEN :startDate AND :endDate
            GROUP BY
                CASE
                    WHEN v.id IS NOT NULL THEN 'vinyl'
                    WHEN c.id IS NOT NULL THEN 'cassette'
                    WHEN cd.id IS NOT NULL THEN 'cd'
                    ELSE 'unknown'
                END
            ORDER BY totalSales DESC
            """, nativeQuery = true)
    List<Object[]> getSalesByType(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Get top selling articles
     */
    @Query(value = """
            SELECT
                aa.id,
                aa.title,
                ar.name as artist_name,
                CASE
                    WHEN v.id IS NOT NULL THEN 'vinyl'
                    WHEN c.id IS NOT NULL THEN 'cassette'
                    WHEN cd.id IS NOT NULL THEN 'cd'
                    ELSE 'unknown'
                END as type,
                aa.image_url,
                COALESCE(SUM(oi.quantity), 0) as totalSold,
                COALESCE(SUM(oi.total_price), 0) as totalRevenue,
                COALESCE(aa.average_rating, 0) as averageRating
            FROM analog_article aa
            JOIN artist ar ON aa.artist_id = ar.id
            LEFT JOIN vinyl v ON aa.id = v.analog_article_id
            LEFT JOIN cassette c ON aa.id = c.analog_article_id
            LEFT JOIN cd ON aa.id = cd.analog_article_id
            JOIN order_item oi ON aa.id = oi.analog_article_id
            JOIN "order" o ON oi.order_id = o.id
            WHERE o.order_status_id = 4
            AND o.created_at >= CURRENT_DATE - INTERVAL ':periodDays days'
            GROUP BY aa.id, aa.title, ar.name, aa.image_url, aa.average_rating, v.id, c.id, cd.id
            HAVING SUM(oi.quantity) > 0
            ORDER BY SUM(oi.quantity) DESC, SUM(oi.total_price) DESC
            LIMIT :limitCount
            """, nativeQuery = true)
    List<Object[]> getTopSellingArticles(@Param("periodDays") Integer periodDays,
            @Param("limitCount") Integer limitCount);

    /**
     * Get top customers by spending
     */
    @Query(value = """
            SELECT
                u.id,
                u.username,
                u.first_name,
                u.last_name,
                u.email,
                COALESCE(u.total_spent, 0) as totalSpent,
                COALESCE(u.total_orders, 0) as totalOrders,
                CASE
                    WHEN u.total_orders > 0 THEN ROUND(u.total_spent / u.total_orders, 2)
                    ELSE 0
                END as averageOrderValue,
                (SELECT MAX(o.created_at) FROM "order" o
                 WHERE o.user_id = u.id AND o.order_status_id = 4) as lastOrderDate
            FROM "user" u
            WHERE u.user_type_id = 1
            AND u.total_spent > 0
            AND u.is_active = true
            ORDER BY u.total_spent DESC
            LIMIT :limitCount
            """, nativeQuery = true)
    List<Object[]> getTopCustomers(@Param("limitCount") Integer limitCount);

    /**
     * Get top rated articles
     */
    @Query(value = """
            SELECT
                aa.id,
                aa.title,
                ar.name as artist_name,
                CASE
                    WHEN v.id IS NOT NULL THEN 'vinyl'
                    WHEN c.id IS NOT NULL THEN 'cassette'
                    WHEN cd.id IS NOT NULL THEN 'cd'
                    ELSE 'unknown'
                END as type,
                aa.image_url,
                COALESCE(aa.average_rating, 0) as averageRating,
                COALESCE(aa.total_ratings, 0) as totalRatings,
                COALESCE(aa.total_sold, 0) as totalSold
            FROM analog_article aa
            JOIN artist ar ON aa.artist_id = ar.id
            LEFT JOIN vinyl v ON aa.id = v.analog_article_id
            LEFT JOIN cassette c ON aa.id = c.analog_article_id
            LEFT JOIN cd ON aa.id = cd.analog_article_id
            WHERE aa.total_ratings >= :minRatings
            AND aa.average_rating > 0
            AND aa.is_available = true
            ORDER BY aa.average_rating DESC, aa.total_ratings DESC
            LIMIT :limitCount
            """, nativeQuery = true)
    List<Object[]> getTopRatedArticles(@Param("minRatings") Integer minRatings,
            @Param("limitCount") Integer limitCount);

    /**
     * Get inventory summary
     */
    @Query(value = """
            SELECT
                COUNT(*) as totalArticles,
                COALESCE(SUM(aa.stock_quantity), 0) as totalStock,
                COUNT(CASE WHEN aa.stock_quantity <= aa.min_stock_level AND aa.stock_quantity > 0 THEN 1 END) as lowStockArticles,
                COUNT(CASE WHEN aa.stock_quantity = 0 THEN 1 END) as outOfStockArticles,
                COALESCE(SUM(aa.stock_quantity * aa.price), 0) as totalInventoryValue
            FROM analog_article aa
            WHERE aa.is_available = true
            """, nativeQuery = true)
    Object[] getInventorySummary();

    /**
     * Get inventory by type
     */
    @Query(value = """
            SELECT
                CASE
                    WHEN v.id IS NOT NULL THEN 'vinyl'
                    WHEN c.id IS NOT NULL THEN 'cassette'
                    WHEN cd.id IS NOT NULL THEN 'cd'
                    ELSE 'unknown'
                END as type,
                COUNT(*) as totalArticles,
                COALESCE(SUM(aa.stock_quantity), 0) as totalStock,
                COALESCE(SUM(aa.stock_quantity * aa.price), 0) as inventoryValue
            FROM analog_article aa
            LEFT JOIN vinyl v ON aa.id = v.analog_article_id
            LEFT JOIN cassette c ON aa.id = c.analog_article_id
            LEFT JOIN cd ON aa.id = cd.analog_article_id
            WHERE aa.is_available = true
            GROUP BY
                CASE
                    WHEN v.id IS NOT NULL THEN 'vinyl'
                    WHEN c.id IS NOT NULL THEN 'cassette'
                    WHEN cd.id IS NOT NULL THEN 'cd'
                    ELSE 'unknown'
                END
            ORDER BY totalArticles DESC
            """, nativeQuery = true)
    List<Object[]> getInventoryByType();

    /**
     * Get inventory by genre
     */
    @Query(value = """
            SELECT
                mg.name as genre,
                COUNT(*) as totalArticles,
                COALESCE(SUM(aa.stock_quantity), 0) as totalStock
            FROM analog_article aa
            JOIN music_genre mg ON aa.music_genre_id = mg.id
            WHERE aa.is_available = true
            GROUP BY mg.name
            ORDER BY totalArticles DESC
            """, nativeQuery = true)
    List<Object[]> getInventoryByGenre();

    /**
     * Get stock alerts (low stock and out of stock)
     */
    @Query(value = """
            SELECT
                aa.id,
                aa.title,
                ar.name as artist_name,
                CASE
                    WHEN v.id IS NOT NULL THEN 'vinyl'
                    WHEN c.id IS NOT NULL THEN 'cassette'
                    WHEN cd.id IS NOT NULL THEN 'cd'
                    ELSE 'unknown'
                END as type,
                aa.stock_quantity,
                aa.min_stock_level,
                aa.image_url,
                CASE
                    WHEN aa.stock_quantity = 0 THEN 'out_of_stock'
                    WHEN aa.stock_quantity <= aa.min_stock_level THEN 'low_stock'
                    ELSE 'normal'
                END as severity
            FROM analog_article aa
            JOIN artist ar ON aa.artist_id = ar.id
            LEFT JOIN vinyl v ON aa.id = v.analog_article_id
            LEFT JOIN cassette c ON aa.id = c.analog_article_id
            LEFT JOIN cd ON aa.id = cd.analog_article_id
            WHERE aa.is_available = true
            AND aa.stock_quantity <= aa.min_stock_level
            ORDER BY
                CASE
                    WHEN aa.stock_quantity = 0 THEN 1
                    ELSE 2
                END,
                aa.stock_quantity ASC
            """, nativeQuery = true)
    List<Object[]> getStockAlerts();
}