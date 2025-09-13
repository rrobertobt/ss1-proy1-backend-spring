package usac.cunoc.bpmn.dto.catalog;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Article comment DTO - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Article comment information")
public class ArticleCommentDto {

    @Schema(description = "Comment ID", example = "1")
    private Integer id;

    @Schema(description = "User information")
    private UserDto user;

    @Schema(description = "Comment text")
    private String comment_text;

    @Schema(description = "Parent comment ID", example = "null")
    private Integer parent_comment_id;

    @Schema(description = "Comment status information")
    private StatusDto status;

    @Schema(description = "Likes count", example = "15")
    private Integer likes_count;

    @Schema(description = "List of replies")
    private List<ArticleCommentDto> replies;

    @Schema(description = "Comment creation date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created_at;

    @Schema(description = "Comment last update date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updated_at;

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
    @Schema(description = "Comment status information")
    public static class StatusDto {
        @Schema(description = "Status ID", example = "1")
        private Integer id;

        @Schema(description = "Status name", example = "Activo")
        private String name;
    }
}