package usac.cunoc.bpmn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import usac.cunoc.bpmn.dto.admin.comment.*;
import usac.cunoc.bpmn.dto.common.ApiResponseDto;
import usac.cunoc.bpmn.repository.UserRepository;
import usac.cunoc.bpmn.service.AdminCommentService;

/**
 * Admin comment controller for comment moderation operations - matches PDF
 * specification exactly
 * Requires ADMIN role for all operations
 */
@RestController
@RequestMapping("/api/v1/admin/comments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
@Tag(name = "Admin Comment Moderation", description = "Administrative comment moderation operations")
public class AdminCommentController {

    private final AdminCommentService adminCommentService;
    private final UserRepository userRepository;

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete comment", description = "Delete a comment with admin reason")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    public ResponseEntity<ApiResponseDto<DeleteCommentResponseDto>> deleteComment(
            @Parameter(description = "Comment ID", required = true) @PathVariable Integer id,
            @Valid @RequestBody DeleteCommentRequestDto request,
            Authentication authentication) {

        Integer adminUserId = getCurrentUserId(authentication);
        DeleteCommentResponseDto response = adminCommentService.deleteComment(id, request, adminUserId);

        return ResponseEntity.ok(ApiResponseDto.success("Comentario eliminado exitosamente", response));
    }

    @GetMapping("/reported")
    @Operation(summary = "Get reported comments", description = "Get paginated list of reported comments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reported comments retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<ApiResponseDto<ReportedCommentsResponseDto>> getReportedComments(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "Items per page") @RequestParam(defaultValue = "10") Integer limit,
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status) {

        ReportedCommentsRequestDto request = new ReportedCommentsRequestDto();
        request.setPage(page);
        request.setLimit(limit);
        request.setStatus(status);

        ReportedCommentsResponseDto response = adminCommentService.getReportedComments(request);

        return ResponseEntity.ok(ApiResponseDto.success(response));
    }

    @PostMapping("/{id}/report")
    @Operation(summary = "Report comment", description = "Report a comment for moderation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comment reported successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    public ResponseEntity<ApiResponseDto<ReportCommentResponseDto>> reportComment(
            @Parameter(description = "Comment ID", required = true) @PathVariable Integer id,
            @Valid @RequestBody ReportCommentRequestDto request) {

        ReportCommentResponseDto response = adminCommentService.reportComment(id, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("Comentario reportado exitosamente", response));
    }

    // PRIVATE HELPER METHODS

    /**
     * Get current user ID from authentication
     */
    private Integer getCurrentUserId(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();
    }
}