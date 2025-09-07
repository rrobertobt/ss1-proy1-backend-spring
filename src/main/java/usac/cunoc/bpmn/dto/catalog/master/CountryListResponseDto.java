package usac.cunoc.bpmn.dto.catalog.master;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Country list response DTO for master data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for countries master data")
public class CountryListResponseDto {

    @Schema(description = "List of countries")
    private List<CountryDto> countries;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Country information")
    public static class CountryDto {
        @Schema(description = "Country ID", example = "1")
        private Integer id;

        @Schema(description = "Country name", example = "Guatemala")
        private String name;

        @Schema(description = "Country code", example = "GT")
        private String countryCode;

        @Schema(description = "Currency information")
        private CurrencyDto currency;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Currency information")
    public static class CurrencyDto {
        @Schema(description = "Currency ID", example = "1")
        private Integer id;

        @Schema(description = "Currency code", example = "GTQ")
        private String code;

        @Schema(description = "Currency symbol", example = "Q")
        private String symbol;
    }
}