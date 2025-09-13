package usac.cunoc.bpmn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usac.cunoc.bpmn.dto.admin.*;
import usac.cunoc.bpmn.entity.ArticleComment;
import usac.cunoc.bpmn.entity.User;
import usac.cunoc.bpmn.repository.ArticleCommentRepository;
import usac.cunoc.bpmn.repository.UserRepository;
import usac.cunoc.bpmn.service.AdminUserService;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin user service implementation - FIXED VERSION
 * Uses only existing UserRepository methods
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserServiceImpl implements AdminUserService {

        private final UserRepository userRepository;
        private final ArticleCommentRepository articleCommentRepository;

        @Override
        @Transactional(readOnly = true)
        public AdminUserListResponseDto getUsers(Integer page, Integer limit, String search,
                        String userType, String status) {

                // Default pagination values
                int pageNumber = page != null && page > 0 ? page - 1 : 0;
                int pageSize = limit != null && limit > 0 ? Math.min(limit, 100) : 10;

                Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

                // Get users with filters using ONLY existing methods
                Page<User> usersPage = getUsersWithFilters(search, userType, status, pageable);

                // Map to DTOs
                List<AdminUserListResponseDto.UserSummaryDto> users = usersPage.getContent().stream()
                                .map(this::mapToUserSummary)
                                .collect(Collectors.toList());

                // Create pagination info
                AdminUserListResponseDto.PaginationDto pagination = new AdminUserListResponseDto.PaginationDto(
                                page != null ? page : 1,
                                (int) usersPage.getTotalPages(),
                                (int) usersPage.getTotalElements(),
                                pageSize);

                log.info("Retrieved {} users with filters - search: {}, userType: {}, status: {}",
                                users.size(), search, userType, status);

                return new AdminUserListResponseDto(users, pagination);
        }

        @Override
        @Transactional(readOnly = true)
        public AdminUserDetailResponseDto getUserById(Integer userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                log.info("Retrieved user details for user ID: {}", userId);
                return mapToUserDetail(user);
        }

        @Override
        public AdminUserDetailResponseDto updateUserStatus(Integer userId, UpdateUserStatusRequestDto request) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                // Update status fields
                if (request.getIs_active() != null) {
                        user.setIsActive(request.getIs_active());
                }

                if (request.getIs_banned() != null) {
                        user.setIsBanned(request.getIs_banned());
                }

                // Log the update for audit purposes
                if (request.getReason() != null && !request.getReason().trim().isEmpty()) {
                        log.info("User status updated for user ID {}: {} - Reason: {}",
                                        userId, request, request.getReason());
                }

                User savedUser = userRepository.save(user);

                log.info("User status updated successfully for user ID: {}", userId);
                return mapToUserDetail(savedUser);
        }

        @Override
        @Transactional(readOnly = true)
        public CommentViolationResponseDto getUserCommentViolations(Integer userId) {
                log.info("Getting comment violations for user ID: {}", userId);

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                // Get deleted comments for this user using the existing repository method
                List<ArticleComment> deletedComments = articleCommentRepository.findDeletedCommentsByUserId(userId);

                // Map to violation DTOs
                List<CommentViolationResponseDto.ViolationDto> violations = deletedComments.stream()
                                .map(this::mapToViolationDto)
                                .collect(Collectors.toList());

                log.info("Found {} comment violations for user {}", violations.size(), userId);

                return new CommentViolationResponseDto(
                                user.getId(),
                                user.getDeletedCommentsCount(),
                                violations);
        }

        // PRIVATE HELPER METHODS

        /**
         * Get users with filters using ONLY existing UserRepository methods
         */
        private Page<User> getUsersWithFilters(String search, String userType, String status, Pageable pageable) {

                // Priority 1: Search filter (highest priority)
                if (search != null && !search.trim().isEmpty()) {
                        return userRepository
                                        .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                                                        search, search, search, search, pageable);
                }

                // Priority 2: UserType filter - using existing method
                if (userType != null && !userType.trim().isEmpty()) {
                        return userRepository.findByUserTypeName(userType, pageable);
                }

                // Priority 3: Status filter - using existing method with boolean combinations
                if (status != null && !status.trim().isEmpty()) {
                        switch (status.toLowerCase()) {
                                case "activo":
                                        return userRepository.findByIsActiveAndIsBanned(true, false, pageable);
                                case "inactivo":
                                        return userRepository.findByIsActiveAndIsBanned(false, false, pageable);
                                case "baneado":
                                        return userRepository.findByIsActiveAndIsBanned(false, true, pageable);
                                default:
                                        // For 'verificado' and other cases, return all users
                                        return userRepository.findAll(pageable);
                        }
                }

                // Default: All users
                return userRepository.findAll(pageable);
        }

        /**
         * Map User entity to UserSummaryDto
         */
        private AdminUserListResponseDto.UserSummaryDto mapToUserSummary(User user) {
                AdminUserListResponseDto.UserSummaryDto dto = new AdminUserListResponseDto.UserSummaryDto();
                dto.setId(user.getId());
                dto.setUsername(user.getUsername());
                dto.setEmail(user.getEmail());
                dto.setFirst_name(user.getFirstName());
                dto.setLast_name(user.getLastName());
                dto.setIs_active(user.getIsActive());
                dto.setIs_banned(user.getIsBanned());
                dto.setIs_verified(user.getIsVerified());
                dto.setTotal_spent(user.getTotalSpent());
                dto.setTotal_orders(user.getTotalOrders());
                dto.setDeleted_comments_count(user.getDeletedCommentsCount());
                dto.setCreated_at(user.getCreatedAt());

                // User type info
                if (user.getUserType() != null) {
                        dto.setUser_type(new AdminUserListResponseDto.user_typeDto(
                                        user.getUserType().getId(),
                                        user.getUserType().getName()));
                }

                return dto;
        }

        /**
         * Map User entity to UserDetailDto
         */
        private AdminUserDetailResponseDto mapToUserDetail(User user) {
                AdminUserDetailResponseDto dto = new AdminUserDetailResponseDto();
                dto.setId(user.getId());
                dto.setUsername(user.getUsername());
                dto.setEmail(user.getEmail());
                dto.setFirst_name(user.getFirstName());
                dto.setLast_name(user.getLastName());
                dto.setBirth_date(user.getBirthDate());
                dto.setPhone(user.getPhone());
                dto.setIs_active(user.getIsActive());
                dto.setIs_banned(user.getIsBanned());
                dto.setIs_verified(user.getIsVerified());
                dto.setIs_2fa_enabled(user.getIs2faEnabled());
                dto.setTotal_spent(user.getTotalSpent());
                dto.setTotal_orders(user.getTotalOrders());
                dto.setDeleted_comments_count(user.getDeletedCommentsCount());
                dto.setFailed_login_attempts(user.getFailedLoginAttempts());
                dto.setLast_login(user.getLastLogin());
                dto.setCreated_at(user.getCreatedAt());

                // Gender info
                if (user.getGender() != null) {
                        dto.setGender(new AdminUserDetailResponseDto.GenderDto(
                                        user.getGender().getId(),
                                        user.getGender().getName()));
                }

                // User type info
                if (user.getUserType() != null) {
                        dto.setUser_type(new AdminUserDetailResponseDto.user_typeDto(
                                        user.getUserType().getId(),
                                        user.getUserType().getName()));
                }

                return dto;
        }

        /**
         * Map ArticleComment to ViolationDto
         */
        private CommentViolationResponseDto.ViolationDto mapToViolationDto(ArticleComment comment) {
                CommentViolationResponseDto.ViolationDto dto = new CommentViolationResponseDto.ViolationDto();

                dto.setId(comment.getId());
                dto.setComment_text(comment.getCommentText());
                dto.setDeleted_reason(comment.getDeletedReason());
                dto.setDeleted_at(comment.getDeletedAt());

                // Article info
                if (comment.getAnalogArticle() != null) {
                        dto.setArticle_id(comment.getAnalogArticle().getId());
                        dto.setArticle_title(comment.getAnalogArticle().getTitle());
                }

                // Deleted by user info
                if (comment.getDeletedByUser() != null) {
                        dto.setDeleted_by_user(new CommentViolationResponseDto.deleted_by_userDto(
                                        comment.getDeletedByUser().getId(),
                                        comment.getDeletedByUser().getUsername()));
                }

                return dto;
        }
}