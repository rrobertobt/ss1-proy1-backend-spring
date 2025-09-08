package usac.cunoc.bpmn.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.EventChatMessage;
import java.time.LocalDateTime;

/**
 * Repository interface for EventChatMessage entity operations
 */
@Repository
public interface EventChatMessageRepository extends JpaRepository<EventChatMessage, Integer> {

    /**
     * Find chat messages for an event with pagination
     */
    @Query("SELECT ecm FROM EventChatMessage ecm " +
           "LEFT JOIN FETCH ecm.user u " +
           "WHERE ecm.event.id = :eventId " +
           "AND (:since IS NULL OR ecm.sentAt >= :since) " +
           "ORDER BY ecm.sentAt DESC")
    Page<EventChatMessage> findChatMessagesByEventId(@Param("eventId") Integer eventId,
                                                      @Param("since") LocalDateTime since,
                                                      Pageable pageable);

    /**
     * Count total messages for an event
     */
    @Query("SELECT COUNT(ecm) FROM EventChatMessage ecm WHERE ecm.event.id = :eventId")
    Integer countByEventId(@Param("eventId") Integer eventId);

    /**
     * Find recent messages since timestamp
     */
    @Query("SELECT ecm FROM EventChatMessage ecm " +
           "LEFT JOIN FETCH ecm.user u " +
           "WHERE ecm.event.id = :eventId " +
           "AND ecm.sentAt >= :since " +
           "ORDER BY ecm.sentAt ASC")
    Page<EventChatMessage> findRecentMessagesByEventId(@Param("eventId") Integer eventId,
                                                        @Param("since") LocalDateTime since,
                                                        Pageable pageable);
}