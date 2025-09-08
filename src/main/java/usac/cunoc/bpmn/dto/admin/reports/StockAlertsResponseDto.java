package usac.cunoc.bpmn.dto.admin.reports;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO for stock alerts response - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Stock alerts response data")
public class StockAlertsResponseDto {

    @Schema(description = "List of articles with low stock")
    private List<StockAlertDto> lowStockAlerts;

    @Schema(description = "List of articles out of stock")
    private List<StockAlertDto> outOfStockAlerts;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Stock alert information")
    public static class StockAlertDto {
        @Schema(description = "Article ID", example = "1")
        private Integer articleId;

        @Schema(description = "Article title", example = "Abbey Road")
        private String title;

        @Schema(description = "Artist name", example = "The Beatles")
        private String artist;

        @Schema(description = "Article type", example = "vinyl")
        private String type;

        @Schema(description = "Current stock quantity", example = "3")
        private Integer currentStock;

        @Schema(description = "Minimum stock level", example = "5")
        private Integer minStockLevel;

        @Schema(description = "Article image URL", example = "https://example.com/image.jpg")
        private String imageUrl;

        @Schema(description = "Alert severity", example = "low")
        private String severity; // "low", "out_of_stock"
    }
}