package usac.cunoc.bpmn.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Common pagination DTO - reusable across all paginated responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Pagination information")
public class PaginationDto {

    @Schema(description = "Current page number", example = "1")
    private Integer currentPage;

    @Schema(description = "Total number of pages", example = "10")
    private Integer totalPages;

    @Schema(description = "Total number of items", example = "95")
    private Integer totalItems;

    @Schema(description = "Items per page", example = "10")
    private Integer itemsPerPage;
}