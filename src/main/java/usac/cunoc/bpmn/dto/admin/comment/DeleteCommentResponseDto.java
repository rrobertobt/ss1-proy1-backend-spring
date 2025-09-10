package usac.cunoc.bpmn.dto.admin.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for delete comment response - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Delete comment response data")
public class DeleteCommentResponseDto {

    @Schema(description = "Deleted comment ID", example = "1")
    private Integer commentId;

    @Schema(description = "Article ID", example = "5")
    private Integer articleId;

    @Schema(description = "User ID who owned the comment", example = "10")
    private Integer userId;

    @Schema(description = "Reason for deletion", example = "Contenido inapropiado")
    private String reason;

    @Schema(description = "Deletion timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deletedAt;

    @Schema(description = "Updated count of deleted comments for the user", example = "1")
    private Integer userDeletedCommentsCount;
}