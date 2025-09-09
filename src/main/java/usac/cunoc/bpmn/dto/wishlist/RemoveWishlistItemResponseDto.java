package usac.cunoc.bpmn.dto.wishlist;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for removing item from wishlist
 * Matches PDF specification exactly for DELETE /api/v1/wishlist/items/{id} response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response after removing article from wishlist")
public class RemoveWishlistItemResponseDto {

    @Schema(description = "ID of the removed wishlist item", example = "10")
    private Integer removedItemId;

    @Schema(description = "Wishlist ID", example = "1")
    private Integer wishlistId;

    @Schema(description = "New total items count in wishlist", example = "2")
    private Integer newTotalItems;
}