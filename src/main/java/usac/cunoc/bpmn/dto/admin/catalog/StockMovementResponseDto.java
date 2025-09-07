package usac.cunoc.bpmn.dto.admin.catalog;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Stock movement response DTO - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for stock movement")
public class StockMovementResponseDto {

    @Schema(description = "Movement ID", example = "1")
    private Integer id;

    @Schema(description = "Article ID", example = "1")
    private Integer articleId;

    @Schema(description = "Movement type", example = "Entrada")
    private String movementType;

    @Schema(description = "Quantity moved", example = "10")
    private Integer quantity;

    @Schema(description = "Previous stock", example = "15")
    private Integer previousStock;

    @Schema(description = "New stock", example = "25")
    private Integer newStock;

    @Schema(description = "Movement creation timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}