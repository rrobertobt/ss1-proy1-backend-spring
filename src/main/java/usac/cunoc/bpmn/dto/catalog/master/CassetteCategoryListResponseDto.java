package usac.cunoc.bpmn.dto.catalog.master;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

/**
 * Cassette category list response DTO for master data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for cassette categories master data")
public class CassetteCategoryListResponseDto {

    @Schema(description = "List of cassette categories")
    private List<CassetteCategoryDto> cassetteCategories;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Cassette category information")
    public static class CassetteCategoryDto {
        @Schema(description = "Category ID", example = "1")
        private Integer id;

        @Schema(description = "Category name", example = "Nuevo")
        private String name;

        @Schema(description = "Discount percentage", example = "0.00")
        private BigDecimal discountPercentage;

        @Schema(description = "Category description")
        private String description;
    }
}