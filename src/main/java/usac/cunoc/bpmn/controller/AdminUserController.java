package usac.cunoc.bpmn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usac.cunoc.bpmn.dto.admin.*;
import usac.cunoc.bpmn.dto.common.ApiResponseDto;
import usac.cunoc.bpmn.service.AdminUserService;
import java.time.LocalDateTime;

/**
 * Admin user controller with responses matching PDF specification exactly
 */
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin User Management", description = "Administrative user management operations")
public class AdminUserController {

        private final AdminUserService adminUserService;

        @GetMapping
        @Operation(summary = "Get users list", description = "Get paginated list of users with filters")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
        })
        public ResponseEntity<ApiResponseDto<AdminUserListResponseDto>> getUsers(
                        @RequestParam(defaultValue = "1") Integer page,
                        @RequestParam(defaultValue = "10") Integer limit,
                        @RequestParam(required = false) String search,
                        @RequestParam(required = false) String userType,
                        @RequestParam(required = false) String status) {

                AdminUserListResponseDto users = adminUserService.getUsers(page, limit, search, userType, status);
                return ResponseEntity.ok(ApiResponseDto.success(users));
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get user details", description = "Get detailed information about a specific user")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User details retrieved successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        public ResponseEntity<ApiResponseDto<AdminUserDetailResponseDto>> getUserById(@PathVariable Integer id) {
                AdminUserDetailResponseDto user = adminUserService.getUserById(id);
                return ResponseEntity.ok(ApiResponseDto.success(user));
        }

        @PutMapping("/{id}/status")
        @Operation(summary = "Update user status", description = "Update user active/banned status")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User status updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        public ResponseEntity<ApiResponseDto<UserStatusUpdateResponseDto>> updateUserStatus(
                        @PathVariable Integer id,
                        @Valid @RequestBody UpdateUserStatusRequestDto request) {

                AdminUserDetailResponseDto updatedUser = adminUserService.updateUserStatus(id, request);

                UserStatusUpdateResponseDto response = new UserStatusUpdateResponseDto(
                                updatedUser.getId(),
                                updatedUser.getIs_active(),
                                updatedUser.getIs_banned(),
                                LocalDateTime.now());

                return ResponseEntity
                                .ok(ApiResponseDto.success("Estado del usuario actualizado exitosamente", response));
        }

        @GetMapping("/{id}/comment-violations")
        @Operation(summary = "Get user comment violations", description = "Get comment violations for a specific user")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Comment violations retrieved successfully"),
                        @ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        public ResponseEntity<ApiResponseDto<CommentViolationResponseDto>> getUserCommentViolations(
                        @PathVariable Integer id) {
                CommentViolationResponseDto violations = adminUserService.getUserCommentViolations(id);
                return ResponseEntity.ok(ApiResponseDto.success(violations));
        }

        // Response DTO for status update response
        public record UserStatusUpdateResponseDto(
                        Integer id,
                        Boolean isActive,
                        Boolean isBanned,
                        LocalDateTime updatedAt) {
        }
}