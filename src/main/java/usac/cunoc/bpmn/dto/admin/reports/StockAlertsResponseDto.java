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

    @Schema(description = "List of articles with low stock or out of stock")
    private List<StockAlertDto> alerts;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Stock alert information")
    public static class StockAlertDto {
        @Schema(description = "Article ID", example = "1")
        private Integer article_id;

        @Schema(description = "Article title", example = "Abbey Road")
        private String title;

        @Schema(description = "Artist name", example = "The Beatles")
        private String artist;

        @Schema(description = "Article type", example = "vinyl")
        private String type;

        @Schema(description = "Current stock quantity", example = "3")
        private Integer current_stock;

        @Schema(description = "Minimum stock level", example = "5")
        private Integer min_stock_level;

        @Schema(description = "Article image URL", example = "https://example.com/image.jpg")
        private String image_url;

        @Schema(description = "Alert severity", example = "low_stock")
        private String severity; // "low_stock", "out_of_stock"
    }
}