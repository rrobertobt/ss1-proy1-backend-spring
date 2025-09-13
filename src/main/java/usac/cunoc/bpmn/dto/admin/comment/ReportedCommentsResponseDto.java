package usac.cunoc.bpmn.dto.admin.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usac.cunoc.bpmn.dto.common.PaginationDto;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for reported comments response - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Reported comments response data")
public class ReportedCommentsResponseDto {

    @Schema(description = "List of reported comments")
    private List<ReportedCommentDto> reportedComments;

    @Schema(description = "Pagination information")
    private PaginationDto pagination;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Individual reported comment information")
    public static class ReportedCommentDto {
        @Schema(description = "Comment ID", example = "1")
        private Integer id;

        @Schema(description = "Comment text", example = "This is a reported comment")
        private String comment_text;

        @Schema(description = "User who posted the comment")
        private UserDto user;

        @Schema(description = "Article information")
        private ArticleDto article;

        @Schema(description = "Number of reports", example = "3")
        private Integer reportCount;

        @Schema(description = "Comment status information")
        private StatusDto status;

        @Schema(description = "Comment creation date")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime created_at;

        @Schema(description = "Date when comment was reported")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime reported_at;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "User information")
    public static class UserDto {
        @Schema(description = "User ID", example = "1")
        private Integer id;

        @Schema(description = "Username", example = "johndoe123")
        private String username;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Article information")
    public static class ArticleDto {
        @Schema(description = "Article ID", example = "5")
        private Integer id;

        @Schema(description = "Article title", example = "Dark Side of the Moon")
        private String title;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Comment status information")
    public static class StatusDto {
        @Schema(description = "Status ID", example = "3")
        private Integer id;

        @Schema(description = "Status name", example = "Reportado")
        private String name;
    }
}