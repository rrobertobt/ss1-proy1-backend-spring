package usac.cunoc.bpmn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usac.cunoc.bpmn.dto.common.PaginationDto;
import usac.cunoc.bpmn.dto.notification.NotificationListResponseDto;
import usac.cunoc.bpmn.dto.notification.MarkNotificationReadResponseDto;
import usac.cunoc.bpmn.dto.notification.MarkAllNotificationsReadResponseDto;
import usac.cunoc.bpmn.entity.User;
import usac.cunoc.bpmn.entity.UserNotification;
import usac.cunoc.bpmn.repository.UserRepository;
import usac.cunoc.bpmn.repository.UserNotificationRepository;
import usac.cunoc.bpmn.service.NotificationService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Notification service implementation - handles all user notification
 * operations
 * 100% compliant with PDF specification and database schema
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final UserNotificationRepository userNotificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public NotificationListResponseDto getUserNotifications(Integer userId, Integer page, Integer limit,
            Boolean unread) {
        // Get user entity
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validate and set pagination parameters
        int pageNumber = page != null && page > 0 ? page - 1 : 0;
        int pageSize = limit != null && limit > 0 ? Math.min(limit, 100) : 10;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        // Convert Boolean unread filter to proper parameter
        Boolean isReadFilter = unread != null ? !unread : null;

        // Get notifications with pagination
        Page<UserNotification> notificationsPage = userNotificationRepository
                .findByUserWithFilter(user, isReadFilter, pageable);

        // Map to DTOs
        List<NotificationListResponseDto.NotificationDto> notifications = notificationsPage.getContent()
                .stream()
                .map(this::mapToNotificationDto)
                .collect(Collectors.toList());

        // Get unread count
        Integer unreadCount = userNotificationRepository.countUnreadByUser(user);

        // Create pagination info
        PaginationDto pagination = new PaginationDto(
                page != null ? page : 1,
                notificationsPage.getTotalPages(),
                (int) notificationsPage.getTotalElements(),
                pageSize);

        log.info("Retrieved {} notifications for user {} (page {}, unread filter: {})",
                notifications.size(), userId, page, unread);

        return new NotificationListResponseDto(notifications, unreadCount, pagination);
    }

    @Override
    @Transactional
    public MarkNotificationReadResponseDto markNotificationAsRead(Integer notificationId, Integer userId) {
        // Get user entity
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Find notification with security validation
        UserNotification notification = userNotificationRepository.findByIdAndUser(notificationId, user)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada o sin permisos"));

        // Check if already read
        if (notification.getIsRead()) {
            log.info("Notification {} already marked as read for user {}", notificationId, userId);
            return new MarkNotificationReadResponseDto(
                    notification.getId(),
                    true,
                    notification.getReadAt());
        }

        // Mark as read
        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());

        UserNotification savedNotification = userNotificationRepository.save(notification);

        log.info("Notification {} marked as read for user {}", notificationId, userId);

        return new MarkNotificationReadResponseDto(
                savedNotification.getId(),
                true,
                savedNotification.getReadAt());
    }

    @Override
    @Transactional
    public MarkAllNotificationsReadResponseDto markAllNotificationsAsRead(Integer userId) {
        // Get user entity
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Get count of unread notifications before update
        Integer unreadCountBefore = userNotificationRepository.countUnreadByUser(user);

        // Mark all as read using batch update
        userNotificationRepository.markAllAsReadByUser(user);

        // Get unread count after update (should be 0)
        Integer unreadCountAfter = userNotificationRepository.countUnreadByUser(user);

        log.info("Marked {} notifications as read for user {}", unreadCountBefore, userId);

        return new MarkAllNotificationsReadResponseDto(unreadCountBefore, unreadCountAfter);
    }

    /**
     * Map UserNotification entity to DTO
     */
    private NotificationListResponseDto.NotificationDto mapToNotificationDto(UserNotification notification) {
        NotificationListResponseDto.NotificationTypeDto typeDto = new NotificationListResponseDto.NotificationTypeDto(
                notification.getNotificationType().getId(),
                notification.getNotificationType().getName());

        return new NotificationListResponseDto.NotificationDto(
                notification.getId(),
                typeDto,
                notification.getTitle(),
                notification.getMessage(),
                notification.getReferenceType(),
                notification.getReferenceId(),
                notification.getIsRead(),
                notification.getIsEmailSent(),
                notification.getCreatedAt(),
                notification.getReadAt());
    }
}