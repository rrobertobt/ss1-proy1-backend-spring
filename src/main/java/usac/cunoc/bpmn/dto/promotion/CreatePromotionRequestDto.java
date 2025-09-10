package usac.cunoc.bpmn.dto.promotion;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Request DTO for creating CD promotion - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new CD promotion")
public class CreatePromotionRequestDto {

    @NotBlank(message = "Promotion name is required")
    @Size(max = 255, message = "Promotion name cannot exceed 255 characters")
    @Schema(description = "Promotion name", example = "Promoción Rock Verano 2024", required = true)
    private String name;

    @NotNull(message = "Promotion type ID is required")
    @Schema(description = "Promotion type ID", example = "1", required = true)
    private Integer promotionTypeId;

    @Schema(description = "Genre ID (required for genre-based promotions)", example = "3")
    private Integer genreId;

    @NotNull(message = "Discount percentage is required")
    @DecimalMin(value = "0.01", message = "Discount percentage must be greater than 0")
    @DecimalMax(value = "100.00", message = "Discount percentage cannot exceed 100")
    @Schema(description = "Discount percentage", example = "15.00", required = true)
    private BigDecimal discountPercentage;

    @NotNull(message = "Max items is required")
    @Min(value = 1, message = "Max items must be at least 1")
    @Max(value = 10, message = "Max items cannot exceed 10")
    @Schema(description = "Maximum items allowed in promotion", example = "4", required = true)
    private Integer maxItems;

    @Schema(description = "Promotion start date")
    private LocalDateTime startDate;

    @Schema(description = "Promotion end date")
    private LocalDateTime endDate;

    @NotEmpty(message = "Article IDs list cannot be empty")
    @Schema(description = "List of article IDs to include in promotion", example = "[1, 2, 3, 4]", required = true)
    private List<Integer> articleIds;
}