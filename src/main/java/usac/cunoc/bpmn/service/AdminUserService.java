package usac.cunoc.bpmn.service;

import usac.cunoc.bpmn.dto.admin.*;

/**
 * Admin user service interface for user administration operations
 */
public interface AdminUserService {

    /**
     * Get paginated list of users with filters
     */
    AdminUserListResponseDto getUsers(Integer page, Integer limit, String search,
            String userType, String status);

    /**
     * Get user details by ID
     */
    AdminUserDetailResponseDto getUserById(Integer userId);

    /**
     * Update user status (active/banned)
     */
    AdminUserDetailResponseDto updateUserStatus(Integer userId, UpdateUserStatusRequestDto request);

    /**
     * Get user comment violations
     */
    CommentViolationResponseDto getUserCommentViolations(Integer userId);
}