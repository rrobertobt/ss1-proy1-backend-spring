package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.Payment;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Payment entity operations
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    /**
     * Find payments by order ID
     */
    @Query("SELECT p FROM Payment p " +
            "LEFT JOIN FETCH p.paymentMethod " +
            "LEFT JOIN FETCH p.paymentStatus " +
            "LEFT JOIN FETCH p.currency " +
            "WHERE p.order.id = :orderId " +
            "ORDER BY p.createdAt DESC")
    List<Payment> findPaymentsByOrderId(@Param("orderId") Integer orderId);

    /**
     * Find payment by payment number
     */
    Optional<Payment> findByPaymentNumber(String paymentNumber);

    /**
     * Check if payment number exists
     */
    boolean existsByPaymentNumber(String paymentNumber);
}