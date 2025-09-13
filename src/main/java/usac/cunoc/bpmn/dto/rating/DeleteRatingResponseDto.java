package usac.cunoc.bpmn.dto.rating;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Response DTO for deleting article rating
 * Matches PDF specification exactly for DELETE
 * /api/v1/articles/{article_id}/ratings/{id}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Article rating deletion response")
public class DeleteRatingResponseDto {

    @Schema(description = "Deleted rating ID", example = "1")
    private Integer rating_id;

    @Schema(description = "Article ID", example = "1")
    private Integer article_id;

    @Schema(description = "Deletion status", example = "true")
    private Boolean is_deleted;

    @Schema(description = "Deletion timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deleted_at;
}