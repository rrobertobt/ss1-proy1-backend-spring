package usac.cunoc.bpmn.dto.rating;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Response DTO for creating article rating
 * Matches PDF specification exactly for POST /api/v1/articles/{id}/ratings
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Article rating creation response")
public class CreateRatingResponseDto {

    @Schema(description = "Rating ID", example = "1")
    private Integer id;

    @Schema(description = "Article ID", example = "1")
    private Integer articleId;

    @Schema(description = "User ID", example = "1")
    private Integer userId;

    @Schema(description = "Rating value", example = "4")
    private Integer rating;

    @Schema(description = "Review text", example = "Excelente álbum, muy recomendado")
    private String reviewText;

    @Schema(description = "Is verified purchase", example = "true")
    private Boolean isVerifiedPurchase;

    @Schema(description = "Creation timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}