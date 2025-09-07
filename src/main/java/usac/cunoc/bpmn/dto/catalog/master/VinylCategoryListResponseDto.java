package usac.cunoc.bpmn.dto.catalog.master;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Vinyl category list response DTO for master data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for vinyl categories master data")
public class VinylCategoryListResponseDto {

    @Schema(description = "List of vinyl categories")
    private List<VinylCategoryDto> vinylCategories;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Vinyl category information")
    public static class VinylCategoryDto {
        @Schema(description = "Category ID", example = "1")
        private Integer id;

        @Schema(description = "Vinyl size", example = "12\"")
        private String size;

        @Schema(description = "Category description")
        private String description;

        @Schema(description = "Typical RPM", example = "33")
        private Integer typicalRpm;
    }
}