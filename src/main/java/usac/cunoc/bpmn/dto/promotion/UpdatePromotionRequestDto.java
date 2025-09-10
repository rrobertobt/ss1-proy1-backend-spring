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
 * Request DTO for updating CD promotion - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update an existing CD promotion")
public class UpdatePromotionRequestDto {

    @NotBlank(message = "Promotion name is required")
    @Size(max = 255, message = "Promotion name cannot exceed 255 characters")
    @Schema(description = "Promotion name", example = "Promoción Rock Actualizada", required = true)
    private String name;

    @NotNull(message = "Discount percentage is required")
    @DecimalMin(value = "0.01", message = "Discount percentage must be greater than 0")
    @DecimalMax(value = "100.00", message = "Discount percentage cannot exceed 100")
    @Schema(description = "Discount percentage", example = "20.00", required = true)
    private BigDecimal discountPercentage;

    @NotNull(message = "Max items is required")
    @Min(value = 1, message = "Max items must be at least 1")
    @Max(value = 10, message = "Max items cannot exceed 10")
    @Schema(description = "Maximum items allowed in promotion", example = "5", required = true)
    private Integer maxItems;

    @Schema(description = "Promotion start date")
    private LocalDateTime startDate;

    @Schema(description = "Promotion end date")
    private LocalDateTime endDate;

    @NotNull(message = "Is active status is required")
    @Schema(description = "Whether promotion is active", example = "true", required = true)
    private Boolean isActive;

    @NotEmpty(message = "Article IDs list cannot be empty")
    @Schema(description = "List of article IDs to include in promotion", example = "[1, 2, 3, 4, 5]", required = true)
    private List<Integer> articleIds;
}