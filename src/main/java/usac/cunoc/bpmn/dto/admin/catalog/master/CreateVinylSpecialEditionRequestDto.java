package usac.cunoc.bpmn.dto.admin.catalog.master;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create vinyl special edition request DTO - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new vinyl special edition")
public class CreateVinylSpecialEditionRequestDto {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    @Schema(description = "Special edition name", example = "Edición Holográfica", required = true)
    private String name;

    @Size(max = 50, message = "Color cannot exceed 50 characters")
    @Schema(description = "Vinyl color", example = "Multicolor holográfico")
    private String color;

    @Schema(description = "Material description", example = "Vinilo con acabado holográfico especial")
    private String material_description;

    @Schema(description = "Extra content included", example = "Poster holográfico y stickers exclusivos")
    private String extra_content;

    @NotNull(message = "Is limited field is required")
    @Schema(description = "Whether this is a limited edition", example = "true", required = true)
    private Boolean is_limited;

    @Min(value = 1, message = "Limited quantity must be at least 1")
    @Schema(description = "Limited quantity (required if is_limited is true)", example = "150")
    private Integer limited_quantity;

    /**
     * Custom validation method called by JSR-303
     */
    @AssertTrue(message = "Limited editions must have a valid limited quantity")
    private boolean isValidlimited_quantity() {
        if (Boolean.TRUE.equals(is_limited)) {
            return limited_quantity != null && limited_quantity > 0;
        }
        return true; // Non-limited editions don't need quantity validation
    }
}