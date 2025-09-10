package usac.cunoc.bpmn.service;

import usac.cunoc.bpmn.dto.admin.comment.*;

/**
 * Admin comment service interface for comment moderation operations
 */
public interface AdminCommentService {

    /**
     * Delete a comment (mark as deleted with reason)
     * 
     * @param commentId   ID of the comment to delete
     * @param request     Delete request with reason
     * @param adminUserId ID of the admin user performing the operation
     * @return Delete comment response data
     */
    DeleteCommentResponseDto deleteComment(Integer commentId, DeleteCommentRequestDto request, Integer adminUserId);

    /**
     * Get reported comments with pagination and filters
     * 
     * @param request Request parameters with pagination and filters
     * @return Paginated list of reported comments
     */
    ReportedCommentsResponseDto getReportedComments(ReportedCommentsRequestDto request);

    /**
     * Report a comment (mark as reported with reason)
     * 
     * @param commentId ID of the comment to report
     * @param request   Report request with reason
     * @return Report comment response data
     */
    ReportCommentResponseDto reportComment(Integer commentId, ReportCommentRequestDto request);
}