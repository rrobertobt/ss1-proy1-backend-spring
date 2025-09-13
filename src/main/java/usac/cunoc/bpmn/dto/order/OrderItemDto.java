package usac.cunoc.bpmn.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usac.cunoc.bpmn.dto.catalog.ArticleBasicDto;
import java.math.BigDecimal;

/**
 * DTO for order item information - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order item information")
public class OrderItemDto {

    @Schema(description = "Order item ID", example = "1")
    private Integer id;

    @Schema(description = "Article information")
    private ArticleBasicDto article;

    @Schema(description = "Quantity ordered", example = "2")
    private Integer quantity;

    @Schema(description = "Unit price at time of order", example = "45.99")
    private BigDecimal unit_price;

    @Schema(description = "Discount amount applied", example = "5.00")
    private BigDecimal discount_amount;

    @Schema(description = "Total price for this item", example = "86.98")
    private BigDecimal total_price;

    @Schema(description = "Applied promotion name", example = "3x2 CD Promotion")
    private String promotion_name;
}