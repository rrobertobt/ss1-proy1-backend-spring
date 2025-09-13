package usac.cunoc.bpmn.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for credit card response - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Credit card response data")
public class CreditCardResponseDto {

    @Schema(description = "Card ID", example = "1")
    private Integer id;

    @Schema(description = "Last four digits", example = "1111")
    private String last_four_digits;

    @Schema(description = "Card brand information")
    private CardBrandDto card_brand;

    @Schema(description = "Cardholder name (decrypted)", example = "John Doe")
    private String card_holder_name;

    @Schema(description = "Expiry month (decrypted)", example = "12")
    private String expiry_month;

    @Schema(description = "Expiry year (decrypted)", example = "2028")
    private String expiry_year;

    @Schema(description = "Is default card", example = "false")
    private Boolean is_default;

    @Schema(description = "Is card active", example = "true")
    private Boolean is_active;

    @Schema(description = "Creation date")
    private LocalDateTime created_at;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Card brand information")
    public static class CardBrandDto {
        @Schema(description = "Brand ID", example = "1")
        private Integer id;

        @Schema(description = "Brand name", example = "Visa")
        private String name;

        @Schema(description = "Brand logo URL")
        private String logo_url;
    }
}