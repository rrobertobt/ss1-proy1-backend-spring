package usac.cunoc.bpmn.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Common currency DTO - reusable across all responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Currency information")
public class CurrencyDto {

    @Schema(description = "Currency code", example = "GTQ")
    private String code;

    @Schema(description = "Currency symbol", example = "Q")
    private String symbol;
}