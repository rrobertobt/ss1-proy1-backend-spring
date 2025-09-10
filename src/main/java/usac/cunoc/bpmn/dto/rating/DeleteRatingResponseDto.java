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
 * /api/v1/articles/{articleId}/ratings/{id}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Article rating deletion response")
public class DeleteRatingResponseDto {

    @Schema(description = "Deleted rating ID", example = "1")
    private Integer ratingId;

    @Schema(description = "Article ID", example = "1")
    private Integer articleId;

    @Schema(description = "Deletion status", example = "true")
    private Boolean isDeleted;

    @Schema(description = "Deletion timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deletedAt;
}