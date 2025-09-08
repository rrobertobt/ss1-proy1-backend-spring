package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.EventRegistration;
import usac.cunoc.bpmn.entity.EventRegistrationId;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for EventRegistration entity operations
 */
@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, EventRegistrationId> {

    /**
     * Check if user is registered for event
     */
    @Query("SELECT COUNT(er) > 0 FROM EventRegistration er " +
           "WHERE er.event.id = :eventId AND er.user.id = :userId")
    boolean existsByEventIdAndUserId(@Param("eventId") Integer eventId, @Param("userId") Integer userId);

    /**
     * Find registration by event and user
     */
    @Query("SELECT er FROM EventRegistration er " +
           "WHERE er.event.id = :eventId AND er.user.id = :userId")
    Optional<EventRegistration> findByEventIdAndUserId(@Param("eventId") Integer eventId, @Param("userId") Integer userId);

    /**
     * Find all registered participants for an event
     */
    @Query("SELECT er FROM EventRegistration er " +
           "LEFT JOIN FETCH er.user u " +
           "WHERE er.event.id = :eventId " +
           "ORDER BY er.registeredAt ASC")
    List<EventRegistration> findRegisteredParticipantsByEventId(@Param("eventId") Integer eventId);

    /**
     * Count registrations for an event
     */
    @Query("SELECT COUNT(er) FROM EventRegistration er WHERE er.event.id = :eventId")
    Integer countByEventId(@Param("eventId") Integer eventId);

    /**
     * Delete registration by event and user
     */
    @Query("DELETE FROM EventRegistration er WHERE er.event.id = :eventId AND er.user.id = :userId")
    void deleteByEventIdAndUserId(@Param("eventId") Integer eventId, @Param("userId") Integer userId);
}