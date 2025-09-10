package usac.cunoc.bpmn.dto.promotion;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for creating CD promotion - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response when creating a new CD promotion")
public class CreatePromotionResponseDto {

    @Schema(description = "Created promotion ID", example = "1")
    private Integer id;

    @Schema(description = "Promotion name", example = "Promoción Rock Verano 2024")
    private String name;

    @Schema(description = "Promotion type details")
    private PromotionTypeDto promotionType;

    @Schema(description = "Discount percentage", example = "15.00")
    private BigDecimal discountPercentage;

    @Schema(description = "Maximum items allowed", example = "4")
    private Integer maxItems;

    @Schema(description = "Promotion start date")
    private LocalDateTime startDate;

    @Schema(description = "Promotion end date")
    private LocalDateTime endDate;

    @Schema(description = "Whether promotion is active", example = "true")
    private Boolean isActive;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Promotion type details")
    public static class PromotionTypeDto {

        @Schema(description = "Promotion type ID", example = "1")
        private Integer id;

        @Schema(description = "Promotion type name", example = "Por Genero")
        private String name;
    }
}