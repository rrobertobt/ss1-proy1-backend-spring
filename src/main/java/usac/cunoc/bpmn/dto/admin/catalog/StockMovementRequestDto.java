package usac.cunoc.bpmn.dto.admin.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Stock movement request DTO - matches PDF JSON structure exactly
 */
@Data
@Schema(description = "Request data for stock movement")
public class StockMovementRequestDto {

    @NotBlank(message = "Movement type is required")
    @Pattern(regexp = "^(Entrada|Salida)$", message = "Movement type must be 'Entrada' or 'Salida'")
    @Schema(description = "Movement type", example = "Entrada", allowableValues = { "Entrada", "Salida" })
    private String movement_type;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be positive")
    @Schema(description = "Quantity to move", example = "10")
    private Integer quantity;

    @NotBlank(message = "Reference type is required")
    @Schema(description = "Reference type", example = "Ajuste Manual")
    private String reference_type;

    @Schema(description = "Reference ID", example = "null")
    private Integer reference_id;

    @Schema(description = "Movement notes")
    private String notes;
}