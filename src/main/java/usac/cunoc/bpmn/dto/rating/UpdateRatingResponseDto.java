package usac.cunoc.bpmn.dto.rating;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Response DTO for updating article rating
 * Matches PDF specification exactly for PUT
 * /api/v1/articles/{article_id}/ratings/{id}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Article rating update response")
public class UpdateRatingResponseDto {

    @Schema(description = "Rating ID", example = "1")
    private Integer id;

    @Schema(description = "Updated rating value", example = "5")
    private Integer rating;

    @Schema(description = "Updated review text", example = "Actualización del review: aún mejor que antes")
    private String review_text;

    @Schema(description = "Update timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updated_at;
}