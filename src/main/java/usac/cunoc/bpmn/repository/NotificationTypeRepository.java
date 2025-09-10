package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.NotificationType;
import java.util.Optional;

/**
 * Repository interface for NotificationType entity operations
 * Handles notification type catalog queries
 */
@Repository
public interface NotificationTypeRepository extends JpaRepository<NotificationType, Integer> {

    /**
     * Find notification type by name - matches database values in Spanish
     * e.g., "Preventa Disponible", "Evento Proximo", "Orden Procesada"
     */
    Optional<NotificationType> findByName(String name);

    /**
     * Check if notification type exists by name
     */
    boolean existsByName(String name);
}