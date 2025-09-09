package usac.cunoc.bpmn.dto.wishlist;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usac.cunoc.bpmn.dto.common.CurrencyDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for wishlist operations
 * Matches PDF specification exactly for GET /api/v1/wishlist response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Complete wishlist with all items")
public class WishlistResponseDto {

    @Schema(description = "Wishlist ID", example = "1")
    private Integer id;

    @Schema(description = "Total number of items in wishlist", example = "3")
    private Integer totalItems;

    @Schema(description = "List of wishlist items")
    private List<WishlistItemDto> items;

    @Schema(description = "Wishlist creation timestamp", example = "2024-03-10T09:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Wishlist last update timestamp", example = "2024-03-15T10:30:00")
    private LocalDateTime updatedAt;

    /**
     * Nested DTO for wishlist items matching PDF structure
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Individual wishlist item with article details")
    public static class WishlistItemDto {

        @Schema(description = "Wishlist item ID", example = "10")
        private Integer id;

        @Schema(description = "Article details")
        private ArticleDto article;

        @Schema(description = "Whether preorder is paid", example = "false")
        private Boolean isPreorderPaid;

        @Schema(description = "Whether notification was sent", example = "false")
        private Boolean notificationSent;

        @Schema(description = "Item creation timestamp", example = "2024-03-15T10:30:00")
        private LocalDateTime createdAt;
    }

    /**
     * Nested DTO for article information matching PDF structure
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Article information in wishlist")
    public static class ArticleDto {

        @Schema(description = "Article ID", example = "5")
        private Integer id;

        @Schema(description = "Article title", example = "Abbey Road")
        private String title;

        @Schema(description = "Artist information")
        private ArtistDto artist;

        @Schema(description = "Article type", example = "vinyl")
        private String type;

        @Schema(description = "Article price", example = "29.99")
        private BigDecimal price;

        @Schema(description = "Currency information")
        private CurrencyDto currency;

        @Schema(description = "Article image URL", example = "https://example.com/image.jpg")
        private String imageUrl;

        @Schema(description = "Whether article is available", example = "true")
        private Boolean isAvailable;

        @Schema(description = "Whether article is preorder", example = "false")
        private Boolean isPreorder;

        @Schema(description = "Stock quantity", example = "15")
        private Integer stockQuantity;
    }

    /**
     * Nested DTO for artist information matching PDF structure
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Artist information")
    public static class ArtistDto {

        @Schema(description = "Artist ID", example = "3")
        private Integer id;

        @Schema(description = "Artist name", example = "The Beatles")
        private String name;
    }
}