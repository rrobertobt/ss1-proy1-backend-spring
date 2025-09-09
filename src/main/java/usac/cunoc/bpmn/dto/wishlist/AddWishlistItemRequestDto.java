package usac.cunoc.bpmn.dto.wishlist;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for adding item to wishlist
 * Matches PDF specification exactly for POST /api/v1/wishlist/items
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to add article to wishlist")
public class AddWishlistItemRequestDto {

    @NotNull(message = "articleId es requerido")
    @Positive(message = "articleId debe ser positivo")
    @Schema(description = "ID of the article to add to wishlist", example = "1", required = true)
    private Integer articleId;
}