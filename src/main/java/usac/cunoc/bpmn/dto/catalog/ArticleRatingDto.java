package usac.cunoc.bpmn.dto.catalog;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Article rating DTO - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Article rating information")
public class ArticleRatingDto {

    @Schema(description = "Rating ID", example = "1")
    private Integer id;

    @Schema(description = "User information")
    private UserDto user;

    @Schema(description = "Rating value (1-5)", example = "4")
    private Integer rating;

    @Schema(description = "Review text")
    private String review_text;

    @Schema(description = "Is verified purchase", example = "true")
    private Boolean is_verified_purchase;

    @Schema(description = "Helpful votes count", example = "8")
    private Integer helpful_votes;

    @Schema(description = "Rating creation date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created_at;

    @Schema(description = "Rating last update date")
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
}