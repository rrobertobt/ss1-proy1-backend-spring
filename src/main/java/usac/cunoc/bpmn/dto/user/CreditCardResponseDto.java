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
    private String lastFourDigits;

    @Schema(description = "Card brand information")
    private CardBrandDto cardBrand;

    @Schema(description = "Cardholder name (decrypted)", example = "John Doe")
    private String cardholderName;

    @Schema(description = "Expiry month (decrypted)", example = "12")
    private String expiryMonth;

    @Schema(description = "Expiry year (decrypted)", example = "2028")
    private String expiryYear;

    @Schema(description = "Is default card", example = "false")
    private Boolean isDefault;

    @Schema(description = "Is card active", example = "true")
    private Boolean isActive;

    @Schema(description = "Creation date")
    private LocalDateTime createdAt;

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
        private String logoUrl;
    }
}