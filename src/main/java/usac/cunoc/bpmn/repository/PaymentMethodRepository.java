package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.PaymentMethod;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for PaymentMethod entity operations
 */
@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {

    /**
     * Find payment method by name (exact match for Spanish values)
     */
    Optional<PaymentMethod> findByName(String name);

    /**
     * Find all active payment methods
     */
    List<PaymentMethod> findByIsActiveTrueOrderByName();

    /**
     * Check if payment method name exists
     */
    boolean existsByName(String name);
}