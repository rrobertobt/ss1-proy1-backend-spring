package usac.cunoc.bpmn.dto.catalog.master;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Currency list response DTO for master data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for currencies master data")
public class CurrencyListResponseDto {

    @Schema(description = "List of currencies")
    private List<CurrencyDto> currencies;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Currency information")
    public static class CurrencyDto {
        @Schema(description = "Currency ID", example = "1")
        private Integer id;

        @Schema(description = "Currency code", example = "USD")
        private String code;

        @Schema(description = "Currency name", example = "US Dollar")
        private String name;

        @Schema(description = "Currency symbol", example = "$")
        private String symbol;
    }
}