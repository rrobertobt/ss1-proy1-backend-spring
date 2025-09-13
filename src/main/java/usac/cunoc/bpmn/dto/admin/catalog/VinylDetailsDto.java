package usac.cunoc.bpmn.dto.admin.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Vinyl-specific details DTO for article creation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Vinyl-specific details")
public class VinylDetailsDto {

    @Schema(description = "Vinyl category ID", example = "1")
    private Integer vinyl_category_id;

    @Schema(description = "Vinyl special edition ID", example = "null")
    private Integer vinyl_special_edition_id;

    @Schema(description = "RPM speed", example = "33", allowableValues = { "33", "45" })
    private Integer rpm = 33;

    @Schema(description = "Is limited edition", example = "false")
    private Boolean is_limited_edition = false;

    @Schema(description = "Remaining limited stock", example = "null")
    private Integer remaining_limited_stock;
}