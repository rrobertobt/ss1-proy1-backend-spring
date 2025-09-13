package usac.cunoc.bpmn.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for creating credit card - matches PDF specification exactly
 */
@Data
@Schema(description = "Create credit card request data")
public class CreateCreditCardRequestDto {

    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "^[0-9]{13,19}$", message = "Card number must be 13-19 digits")
    @Schema(description = "Credit card number", example = "4111111111111111")
    private String card_number;

    @NotBlank(message = "Cardholder name is required")
    @Size(max = 255, message = "Cardholder name must not exceed 255 characters")
    @Schema(description = "Cardholder name", example = "John Doe")
    private String card_holder_name;

    @NotBlank(message = "Expiry month is required")
    @Pattern(regexp = "^(0[1-9]|1[0-2])$", message = "Expiry month must be 01-12")
    @Schema(description = "Expiry month", example = "12")
    private String expiry_month;

    @NotBlank(message = "Expiry year is required")
    @Pattern(regexp = "^[0-9]{4}$", message = "Expiry year must be 4 digits")
    @Schema(description = "Expiry year", example = "2028")
    private String expiry_year;

    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV must be 3-4 digits")
    @Schema(description = "Card CVV", example = "123")
    private String cvv;

    @NotNull(message = "Card brand ID is required")
    @Schema(description = "Card brand ID", example = "1")
    private Integer card_brand_id;

    @Schema(description = "Set as default card", example = "false")
    private Boolean is_default;
}