package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.PaymentStatus;
import java.util.Optional;

/**
 * Repository interface for PaymentStatus entity operations
 */
@Repository
public interface PaymentStatusRepository extends JpaRepository<PaymentStatus, Integer> {

    /**
     * Find payment status by name (exact match for Spanish values)
     */
    Optional<PaymentStatus> findByName(String name);

    /**
     * Check if payment status name exists
     */
    boolean existsByName(String name);
}