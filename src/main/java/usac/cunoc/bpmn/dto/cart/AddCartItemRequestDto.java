package usac.cunoc.bpmn.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for adding item to cart - matches PDF JSON structure
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to add item to shopping cart")
public class AddCartItemRequestDto {

    @NotNull(message = "Article ID is required")
    @Schema(description = "Article ID", example = "1", required = true)
    private Integer article_id;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Schema(description = "Quantity to add", example = "2", required = true)
    private Integer quantity;
}