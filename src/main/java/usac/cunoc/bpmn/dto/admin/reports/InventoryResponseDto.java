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
    private List<TypeInventoryDto> by_type;

    @Schema(description = "Inventory breakdown by music genre")
    private List<GenreInventoryDto> by_genre;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Inventory summary")
    public static class SummaryDto {
        @Schema(description = "Total number of articles", example = "250")
        private Integer total_articles;

        @Schema(description = "Total stock units", example = "1500")
        private Integer total_stock;

        @Schema(description = "Articles with low stock", example = "15")
        private Integer low_stock_aticles;

        @Schema(description = "Articles out of stock", example = "5")
        private Integer out_of_stock_articles;

        @Schema(description = "Total inventory value", example = "75000.50")
        private BigDecimal total_inventory_value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Inventory by type")
    public static class TypeInventoryDto {
        @Schema(description = "Article type", example = "vinyl")
        private String type;

        @Schema(description = "Total articles of this type", example = "100")
        private Integer total_articles;

        @Schema(description = "Total stock for this type", example = "600")
        private Integer total_stock;

        @Schema(description = "Inventory value for this type", example = "30000.25")
        private BigDecimal inventory_value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Inventory by genre")
    public static class GenreInventoryDto {
        @Schema(description = "Music genre", example = "Rock")
        private String genre;

        @Schema(description = "Total articles in this genre", example = "75")
        private Integer total_articles;

        @Schema(description = "Total stock for this genre", example = "450")
        private Integer total_stock;
    }
}