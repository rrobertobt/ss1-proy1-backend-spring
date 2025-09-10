package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.OrderItem;
import java.util.List;

/**
 * Repository interface for OrderItem entity operations
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

        /**
         * Find order items by order ID
         */
        @Query("SELECT oi FROM OrderItem oi " +
                        "LEFT JOIN FETCH oi.analogArticle aa " +
                        "LEFT JOIN FETCH aa.artist " +
                        "LEFT JOIN FETCH aa.currency " +
                        "WHERE oi.order.id = :orderId")
        List<OrderItem> findOrderItemsByOrderId(@Param("orderId") Integer orderId);

        /**
         * Find order items by order ID with full fetch
         */
        @Query("SELECT oi FROM OrderItem oi " +
                        "LEFT JOIN FETCH oi.analogArticle aa " +
                        "LEFT JOIN FETCH aa.artist " +
                        "LEFT JOIN FETCH aa.currency " +
                        "LEFT JOIN FETCH aa.musicGenre " +
                        "LEFT JOIN FETCH oi.cdPromotion " +
                        "WHERE oi.order.id = :orderId " +
                        "ORDER BY oi.createdAt ASC")
        List<OrderItem> findOrderItemsWithDetailsByOrderId(@Param("orderId") Integer orderId);

        /**
         * Check if user has purchased an article and order was delivered
         * Used for verified purchase validation in ratings
         */
        @Query("SELECT COUNT(oi) > 0 FROM OrderItem oi " +
                        "INNER JOIN oi.order o " +
                        "WHERE o.user.id = :userId " +
                        "AND oi.analogArticle.id = :articleId " +
                        "AND o.orderStatus.name = 'Entregado'")
        boolean existsByUserIdAndArticleIdAndDelivered(@Param("userId") Integer userId,
                        @Param("articleId") Integer articleId);

}