package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.EventStatus;
import java.util.Optional;

/**
 * Repository interface for EventStatus entity operations
 */
@Repository
public interface EventStatusRepository extends JpaRepository<EventStatus, Integer> {

    /**
     * Find event status by name
     */
    Optional<EventStatus> findByName(String name);

    /**
     * Check if status allows registration
     */
    boolean existsByNameAndAllowsRegistrationTrue(String name);
}