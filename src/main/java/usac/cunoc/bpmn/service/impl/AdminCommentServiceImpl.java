package usac.cunoc.bpmn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usac.cunoc.bpmn.dto.admin.comment.*;
import usac.cunoc.bpmn.dto.common.PaginationDto;
import usac.cunoc.bpmn.entity.*;
import usac.cunoc.bpmn.repository.*;
import usac.cunoc.bpmn.service.AdminCommentService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin comment service implementation for comment moderation operations
 * WORKING VERSION - Properly registered as Spring Bean
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCommentServiceImpl implements AdminCommentService {

        private final ArticleCommentRepository articleCommentRepository;
        private final CommentStatusRepository commentStatusRepository;
        private final UserRepository userRepository;

        @Override
        @Transactional
        public DeleteCommentResponseDto deleteComment(Integer commentId, DeleteCommentRequestDto request,
                        Integer adminUserId) {
                log.info("Admin user {} attempting to delete comment {}", adminUserId, commentId);

                // Find the comment
                ArticleComment comment = articleCommentRepository.findById(commentId)
                                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));

                // Validate admin user exists
                User adminUser = userRepository.findById(adminUserId)
                                .orElseThrow(() -> new RuntimeException("Usuario administrador no encontrado"));

                // Get "Eliminado" status (ID 2 according to SQL data)
                CommentStatus deletedStatus = commentStatusRepository.findByName("Eliminado")
                                .orElseThrow(() -> new RuntimeException("Estado 'Eliminado' no encontrado"));

                // Update comment as deleted
                comment.setCommentStatus(deletedStatus);
                comment.setDeletedByUser(adminUser);
                comment.setDeletedReason(request.getReason());
                comment.setDeletedAt(LocalDateTime.now());
                comment.setUpdatedAt(LocalDateTime.now());

                ArticleComment savedComment = articleCommentRepository.save(comment);

                // Get updated user deleted comments count (trigger will increment it)
                User commentOwner = userRepository.findById(savedComment.getUser().getId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Usuario propietario del comentario no encontrado"));

                log.info("Comment {} deleted successfully by admin {}", commentId, adminUserId);

                return new DeleteCommentResponseDto(
                                savedComment.getId(),
                                savedComment.getAnalogArticle().getId(),
                                savedComment.getUser().getId(),
                                savedComment.getDeletedReason(),
                                savedComment.getDeletedAt(),
                                commentOwner.getDeletedCommentsCount());
        }

        @Override
        @Transactional(readOnly = true)
        public ReportedCommentsResponseDto getReportedComments(ReportedCommentsRequestDto request) {
                log.info("Getting reported comments with filters: page={}, limit={}, status={}",
                                request.getPage(), request.getLimit(), request.getStatus());

                Pageable pageable = PageRequest.of(
                                request.getPage() - 1,
                                request.getLimit(),
                                Sort.by(Sort.Direction.DESC, "updatedAt"));

                Page<ArticleComment> commentsPage;

                if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
                        // Filter by specific status
                        commentsPage = articleCommentRepository.findByCommentStatusName(request.getStatus(), pageable);
                } else {
                        // Get only reported comments (status = "Reportado")
                        commentsPage = articleCommentRepository.findByCommentStatusName("Reportado", pageable);
                }

                List<ReportedCommentsResponseDto.ReportedCommentDto> reportedComments = commentsPage.getContent()
                                .stream()
                                .map(this::mapToReportedCommentDto)
                                .collect(Collectors.toList());

                PaginationDto pagination = new PaginationDto(
                                request.getPage(),
                                (int) commentsPage.getTotalPages(),
                                (int) commentsPage.getTotalElements(),
                                request.getLimit());

                return new ReportedCommentsResponseDto(reportedComments, pagination);
        }

        @Override
        @Transactional
        public ReportCommentResponseDto reportComment(Integer commentId, ReportCommentRequestDto request) {
                log.info("Reporting comment {} with reason: {}", commentId, request.getReason());

                // Find the comment
                ArticleComment comment = articleCommentRepository.findById(commentId)
                                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));

                // Get "Reportado" status (ID 3 according to SQL data)
                CommentStatus reportedStatus = commentStatusRepository.findByName("Reportado")
                                .orElseThrow(() -> new RuntimeException("Estado 'Reportado' no encontrado"));

                // Update comment as reported
                comment.setCommentStatus(reportedStatus);
                comment.setUpdatedAt(LocalDateTime.now());

                ArticleComment savedComment = articleCommentRepository.save(comment);

                log.info("Comment {} reported successfully", commentId);

                return new ReportCommentResponseDto(
                                savedComment.getId(),
                                savedComment.getUpdatedAt());
        }

        // PRIVATE HELPER METHODS

        /**
         * Map ArticleComment entity to ReportedCommentDto
         */
        private ReportedCommentsResponseDto.ReportedCommentDto mapToReportedCommentDto(ArticleComment comment) {
                ReportedCommentsResponseDto.ReportedCommentDto dto = new ReportedCommentsResponseDto.ReportedCommentDto();

                dto.setId(comment.getId());
                dto.setComment_text(comment.getCommentText());
                dto.setCreated_at(comment.getCreatedAt());
                dto.setReported_at(comment.getUpdatedAt()); // When status was changed to "Reportado"
                dto.setReportCount(1); // Simplified - in real implementation would count actual reports

                // User info
                if (comment.getUser() != null) {
                        dto.setUser(new ReportedCommentsResponseDto.UserDto(
                                        comment.getUser().getId(),
                                        comment.getUser().getUsername()));
                }

                // Article info
                if (comment.getAnalogArticle() != null) {
                        dto.setArticle(new ReportedCommentsResponseDto.ArticleDto(
                                        comment.getAnalogArticle().getId(),
                                        comment.getAnalogArticle().getTitle()));
                }

                // Status info
                if (comment.getCommentStatus() != null) {
                        dto.setStatus(new ReportedCommentsResponseDto.StatusDto(
                                        comment.getCommentStatus().getId(),
                                        comment.getCommentStatus().getName()));
                }

                return dto;
        }
}