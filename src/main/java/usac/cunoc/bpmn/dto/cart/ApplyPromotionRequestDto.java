package usac.cunoc.bpmn.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Request DTO for applying CD promotion - matches PDF JSON structure
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to apply CD promotion to cart items")
public class ApplyPromotionRequestDto {

    @NotNull(message = "Promotion ID is required")
    @Schema(description = "Promotion ID", example = "1", required = true)
    private Integer promotionId;

    @NotEmpty(message = "Article IDs list cannot be empty")
    @Schema(description = "List of article IDs to apply promotion to", example = "[1, 2, 3]", required = true)
    private List<Integer> articleIds;
}