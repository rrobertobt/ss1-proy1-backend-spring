package usac.cunoc.bpmn.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import usac.cunoc.bpmn.entity.User;
import usac.cunoc.bpmn.entity.UserNotification;
import java.util.Optional;

/**
 * Repository interface for UserNotification entity operations
 * Handles user notification queries with optimized indexing
 */
@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Integer> {

    /**
     * Find user notifications with pagination and read status filter
     */
    @Query("SELECT un FROM UserNotification un " +
            "LEFT JOIN FETCH un.notificationType nt " +
            "WHERE un.user = :user " +
            "AND (:unread IS NULL OR un.isRead = :unread) " +
            "ORDER BY un.createdAt DESC")
    Page<UserNotification> findByUserWithFilter(@Param("user") User user,
            @Param("unread") Boolean unread,
            Pageable pageable);

    /**
     * Find notification by ID and user for security validation
     */
    @Query("SELECT un FROM UserNotification un " +
            "LEFT JOIN FETCH un.notificationType nt " +
            "WHERE un.id = :id AND un.user = :user")
    Optional<UserNotification> findByIdAndUser(@Param("id") Integer id, @Param("user") User user);

    /**
     * Count unread notifications for user
     */
    @Query("SELECT COUNT(un) FROM UserNotification un WHERE un.user = :user AND un.isRead = false")
    Integer countUnreadByUser(@Param("user") User user);

    /**
     * Mark all notifications as read for user
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserNotification un SET un.isRead = true, un.readAt = CURRENT_TIMESTAMP " +
            "WHERE un.user = :user AND un.isRead = false")
    void markAllAsReadByUser(@Param("user") User user);

    /**
     * Get total notifications count for user
     */
    @Query("SELECT COUNT(un) FROM UserNotification un WHERE un.user = :user")
    Integer countByUser(@Param("user") User user);
}