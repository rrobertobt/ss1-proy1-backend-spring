package usac.cunoc.bpmn.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.Event;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository interface for Event entity operations
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {

    /**
     * Find event by ID with full details including status and article
     */
    @Query("SELECT e FROM Event e " +
           "LEFT JOIN FETCH e.eventStatus " +
           "LEFT JOIN FETCH e.analogArticle aa " +
           "LEFT JOIN FETCH aa.artist " +
           "LEFT JOIN FETCH e.createdByUser " +
           "WHERE e.id = :eventId")
    Optional<Event> findByIdWithDetails(@Param("eventId") Integer eventId);

    /**
     * Find events with pagination and filters
     */
    @Query("SELECT e FROM Event e " +
           "LEFT JOIN FETCH e.eventStatus es " +
           "LEFT JOIN FETCH e.analogArticle aa " +
           "LEFT JOIN FETCH aa.artist " +
           "WHERE (:status IS NULL OR es.name = :status) " +
           "AND (:upcoming IS NULL OR (:upcoming = true AND e.startDatetime > :currentTime) OR (:upcoming = false)) " +
           "AND (:past IS NULL OR (:past = true AND e.endDatetime < :currentTime) OR (:past = false)) " +
           "ORDER BY e.startDatetime ASC")
    Page<Event> findEventsWithFilters(@Param("status") String status,
                                      @Param("upcoming") Boolean upcoming,
                                      @Param("past") Boolean past,
                                      @Param("currentTime") LocalDateTime currentTime,
                                      Pageable pageable);

    /**
     * Find upcoming events
     */
    @Query("SELECT e FROM Event e " +
           "WHERE e.startDatetime > :currentTime " +
           "ORDER BY e.startDatetime ASC")
    Page<Event> findUpcomingEvents(@Param("currentTime") LocalDateTime currentTime, Pageable pageable);

    /**
     * Find past events
     */
    @Query("SELECT e FROM Event e " +
           "WHERE e.endDatetime < :currentTime " +
           "ORDER BY e.startDatetime DESC")
    Page<Event> findPastEvents(@Param("currentTime") LocalDateTime currentTime, Pageable pageable);

    /**
     * Find events by status
     */
    @Query("SELECT e FROM Event e " +
           "JOIN e.eventStatus es " +
           "WHERE es.name = :statusName " +
           "ORDER BY e.startDatetime ASC")
    Page<Event> findByEventStatusName(@Param("statusName") String statusName, Pageable pageable);
}