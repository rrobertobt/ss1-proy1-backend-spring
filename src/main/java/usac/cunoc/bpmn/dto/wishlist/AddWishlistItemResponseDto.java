package usac.cunoc.bpmn.dto.wishlist;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Response DTO for adding item to wishlist
 * Matches PDF specification exactly for POST /api/v1/wishlist/items response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response after adding article to wishlist")
public class AddWishlistItemResponseDto {

    @Schema(description = "Wishlist ID", example = "1")
    private Integer wishlist_id;

    @Schema(description = "Wishlist item ID", example = "10")
    private Integer item_id;

    @Schema(description = "Article ID that was added", example = "5")
    private Integer article_id;

    @Schema(description = "Item creation timestamp", example = "2024-03-15T10:30:00")
    private LocalDateTime created_at;
}