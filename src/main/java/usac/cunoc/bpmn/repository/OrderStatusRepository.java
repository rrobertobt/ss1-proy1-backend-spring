package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.OrderStatus;
import java.util.Optional;

/**
 * Repository interface for OrderStatus entity operations
 */
@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus, Integer> {

    /**
     * Find order status by name (exact match for Spanish values)
     */
    Optional<OrderStatus> findByName(String name);

    /**
     * Check if order status name exists
     */
    boolean existsByName(String name);
}