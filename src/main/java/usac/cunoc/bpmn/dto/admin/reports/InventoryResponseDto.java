package usac.cunoc.bpmn.dto.admin.reports;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for inventory report response - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Inventory report response data")
public class InventoryResponseDto {

    @Schema(description = "Inventory summary")
    private SummaryDto summary;

    @Schema(description = "Inventory breakdown by article type")
    private List<TypeInventoryDto> byType;

    @Schema(description = "Inventory breakdown by music genre")
    private List<GenreInventoryDto> byGenre;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Inventory summary")
    public static class SummaryDto {
        @Schema(description = "Total number of articles", example = "250")
        private Integer totalArticles;

        @Schema(description = "Total stock units", example = "1500")
        private Integer totalStock;

        @Schema(description = "Articles with low stock", example = "15")
        private Integer lowStockArticles;

        @Schema(description = "Articles out of stock", example = "5")
        private Integer outOfStockArticles;

        @Schema(description = "Total inventory value", example = "75000.50")
        private BigDecimal totalInventoryValue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Inventory by type")
    public static class TypeInventoryDto {
        @Schema(description = "Article type", example = "vinyl")
        private String type;

        @Schema(description = "Total articles of this type", example = "100")
        private Integer totalArticles;

        @Schema(description = "Total stock for this type", example = "600")
        private Integer totalStock;

        @Schema(description = "Inventory value for this type", example = "30000.25")
        private BigDecimal inventoryValue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Inventory by genre")
    public static class GenreInventoryDto {
        @Schema(description = "Music genre", example = "Rock")
        private String genre;

        @Schema(description = "Total articles in this genre", example = "75")
        private Integer totalArticles;

        @Schema(description = "Total stock for this genre", example = "450")
        private Integer totalStock;
    }
}