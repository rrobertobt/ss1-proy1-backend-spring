package usac.cunoc.bpmn.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.Order;
import usac.cunoc.bpmn.entity.User;
import java.util.Optional;

/**
 * Repository interface for Order entity operations
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    /**
     * Find orders by user with pagination
     */
    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.orderStatus " +
            "LEFT JOIN FETCH o.currency " +
            "WHERE o.user = :user " +
            "ORDER BY o.createdAt DESC")
    Page<Order> findOrdersByUser(@Param("user") User user, Pageable pageable);

    /**
     * Find orders by user and status with pagination
     */
    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.orderStatus " +
            "LEFT JOIN FETCH o.currency " +
            "WHERE o.user = :user AND o.orderStatus.name = :status " +
            "ORDER BY o.createdAt DESC")
    Page<Order> findOrdersByUserAndStatus(@Param("user") User user, @Param("status") String status, Pageable pageable);

    /**
     * Find order by ID and user (security constraint)
     */
    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.orderStatus " +
            "LEFT JOIN FETCH o.currency " +
            "LEFT JOIN FETCH o.shippingAddress " +
            "LEFT JOIN FETCH o.billingAddress " +
            "WHERE o.id = :orderId AND o.user = :user")
    Optional<Order> findOrderByIdAndUser(@Param("orderId") Integer orderId, @Param("user") User user);

    /**
     * Find order by order number
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Check if order number exists
     */
    boolean existsByOrderNumber(String orderNumber);

    /**
     * Count orders by user
     */
    long countByUser(User user);

    /**
     * Count orders by user and status
     */
    long countByUserAndOrderStatusName(User user, String status);
}