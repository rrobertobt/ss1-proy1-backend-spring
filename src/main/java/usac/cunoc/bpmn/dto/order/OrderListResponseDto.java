package usac.cunoc.bpmn.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usac.cunoc.bpmn.dto.common.PaginationDto;
import java.util.List;

/**
 * Response DTO for order list - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for orders list")
public class OrderListResponseDto {

    @Schema(description = "List of orders")
    private List<OrderSummaryDto> orders;

    @Schema(description = "Pagination information")
    private PaginationDto pagination;
}