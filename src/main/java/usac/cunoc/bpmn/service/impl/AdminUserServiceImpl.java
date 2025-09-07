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
import usac.cunoc.bpmn.entity.User;
import usac.cunoc.bpmn.repository.UserRepository;
import usac.cunoc.bpmn.service.AdminUserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin user service implementation - compliant with database schema and PDF
 * specification
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;

    @Override
    public AdminUserListResponseDto getUsers(Integer page, Integer limit, String search,
            String userType, String status) {
        // Default pagination values
        int pageNumber = page != null && page > 0 ? page - 1 : 0;
        int pageSize = limit != null && limit > 0 ? limit : 10;

        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        // Get filtered users using custom query
        Page<User> userPage = getUsersWithFilters(search, userType, status, pageable);

        List<AdminUserListResponseDto.UserSummaryDto> userSummaries = userPage.getContent()
                .stream()
                .map(this::mapToUserSummary)
                .collect(Collectors.toList());

        AdminUserListResponseDto.PaginationDto pagination = new AdminUserListResponseDto.PaginationDto(
                pageNumber + 1, // Convert back to 1-based
                userPage.getTotalPages(),
                (int) userPage.getTotalElements(),
                pageSize);

        return new AdminUserListResponseDto(userSummaries, pagination);
    }

    @Override
    public AdminUserDetailResponseDto getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return mapToUserDetail(user);
    }

    @Override
    @Transactional
    public AdminUserDetailResponseDto updateUserStatus(Integer userId, UpdateUserStatusRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Update status fields
        user.setIsActive(request.getIsActive());
        user.setIsBanned(request.getIsBanned());

        // Log the change reason if provided
        if (request.getReason() != null && !request.getReason().trim().isEmpty()) {
            log.info("User status updated for user ID {}: {} - Reason: {}",
                    userId, request, request.getReason());
        }

        User savedUser = userRepository.save(user);
        return mapToUserDetail(savedUser);
    }

    @Override
    public CommentViolationResponseDto getUserCommentViolations(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Note: This would require a proper ArticleComment repository and entity
        // For now, returning basic structure with empty violations
        // In a complete implementation, you would query the article_comment table
        // for comments where comment_status_id = 2 (Eliminado) and user_id = userId

        return new CommentViolationResponseDto(
                user.getId(),
                user.getDeletedCommentsCount(),
                List.of() // Empty list - would be populated with actual violations in full implementation
        );
    }

    // PRIVATE HELPER METHODS

    private Page<User> getUsersWithFilters(String search, String userType, String status, Pageable pageable) {
        // This would typically use Spring Data JPA Specifications or custom queries
        // For now, implementing basic filtering logic

        if (search != null && !search.trim().isEmpty()) {
            String searchTerm = "%" + search.toLowerCase() + "%";
            return userRepository
                    .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                            searchTerm, searchTerm, searchTerm, searchTerm, pageable);
        }

        return userRepository.findAll(pageable);
    }

    private AdminUserListResponseDto.UserSummaryDto mapToUserSummary(User user) {
        AdminUserListResponseDto.UserTypeDto userTypeDto = new AdminUserListResponseDto.UserTypeDto(
                user.getUserType().getId(), user.getUserType().getName());

        return new AdminUserListResponseDto.UserSummaryDto(
                user.getId(), user.getUsername(), user.getEmail(),
                user.getFirstName(), user.getLastName(), userTypeDto,
                user.getIsActive(), user.getIsBanned(), user.getIsVerified(),
                user.getTotalSpent(), user.getTotalOrders(),
                user.getDeletedCommentsCount(), user.getCreatedAt(),
                user.getLastLogin());
    }

    private AdminUserDetailResponseDto mapToUserDetail(User user) {
        AdminUserDetailResponseDto.GenderDto genderDto = null;
        if (user.getGender() != null) {
            genderDto = new AdminUserDetailResponseDto.GenderDto(
                    user.getGender().getId(), user.getGender().getName());
        }

        AdminUserDetailResponseDto.UserTypeDto userTypeDto = new AdminUserDetailResponseDto.UserTypeDto(
                user.getUserType().getId(), user.getUserType().getName());

        return new AdminUserDetailResponseDto(
                user.getId(), user.getUsername(), user.getEmail(),
                user.getFirstName(), user.getLastName(), genderDto,
                user.getBirthDate(), user.getPhone(), userTypeDto,
                user.getIsActive(), user.getIsBanned(), user.getIsVerified(),
                user.getIs2faEnabled(), user.getTotalSpent(), user.getTotalOrders(),
                user.getDeletedCommentsCount(), user.getFailedLoginAttempts(),
                user.getLastLogin(), user.getCreatedAt(),
                List.of(), // addresses - would be populated in full implementation
                List.of() // orderHistory - would be populated in full implementation
        );
    }
}