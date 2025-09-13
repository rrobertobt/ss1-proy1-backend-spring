package usac.cunoc.bpmn.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for comment violations response - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Comment violations response data")
public class CommentViolationResponseDto {

    @Schema(description = "User ID", example = "1")
    private Integer user_id;

    @Schema(description = "Number of deleted comments", example = "2")
    private Integer deleted_comments_count;

    @Schema(description = "List of comment violations")
    private List<ViolationDto> violations;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Individual violation information")
    public static class ViolationDto {
        @Schema(description = "Comment ID", example = "1")
        private Integer id;

        @Schema(description = "Article ID", example = "5")
        private Integer article_id;

        @Schema(description = "Article title", example = "Dark Side of the Moon")
        private String article_title;

        @Schema(description = "Comment text", example = "This is inappropriate content")
        private String comment_text;

        @Schema(description = "Reason for deletion", example = "Contenido inapropiado")
        private String deleted_reason;

        @Schema(description = "When comment was deleted")
        private LocalDateTime deleted_at;

        @Schema(description = "User who deleted the comment")
        private deleted_by_userDto deleted_by_user;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "User who deleted the comment")
    public static class deleted_by_userDto {
        @Schema(description = "Admin user ID", example = "2")
        private Integer id;

        @Schema(description = "Admin username", example = "admin")
        private String username;
    }
}