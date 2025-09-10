package usac.cunoc.bpmn.service;

import usac.cunoc.bpmn.dto.notification.NotificationListResponseDto;
import usac.cunoc.bpmn.dto.notification.MarkNotificationReadResponseDto;
import usac.cunoc.bpmn.dto.notification.MarkAllNotificationsReadResponseDto;

/**
 * Notification service interface for user notification operations
 */
public interface NotificationService {

    /**
     * Get user notifications with pagination and filters
     * 
     * @param userId User ID
     * @param page   Page number (1-based)
     * @param limit  Items per page
     * @param unread Filter by read status (null for all, true for unread only,
     *               false for read only)
     * @return Paginated notifications response
     */
    NotificationListResponseDto getUserNotifications(Integer userId, Integer page, Integer limit, Boolean unread);

    /**
     * Mark specific notification as read
     * 
     * @param notificationId Notification ID
     * @param userId         User ID for security validation
     * @return Mark as read response
     */
    MarkNotificationReadResponseDto markNotificationAsRead(Integer notificationId, Integer userId);

    /**
     * Mark all user notifications as read
     * 
     * @param userId User ID
     * @return Mark all as read response
     */
    MarkAllNotificationsReadResponseDto markAllNotificationsAsRead(Integer userId);
}