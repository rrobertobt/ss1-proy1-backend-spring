package usac.cunoc.bpmn.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for creating user address - matches PDF specification exactly
 */
@Data
@Schema(description = "Create address request data")
public class CreateAddressRequestDto {

    @NotBlank(message = "Address line 1 is required")
    @Size(max = 255, message = "Address line 1 must not exceed 255 characters")
    @Schema(description = "Primary address line", example = "123 Main Street")
    private String addressLine1;

    @Size(max = 255, message = "Address line 2 must not exceed 255 characters")
    @Schema(description = "Secondary address line", example = "Apt 4B")
    private String addressLine2;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    @Schema(description = "City", example = "Guatemala City")
    private String city;

    @Size(max = 100, message = "State must not exceed 100 characters")
    @Schema(description = "State or province", example = "Guatemala")
    private String state;

    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    @Schema(description = "Postal code", example = "01001")
    private String postalCode;

    @NotNull(message = "Country ID is required")
    @Schema(description = "Country ID", example = "1")
    private Integer countryId;

    @Schema(description = "Set as default address", example = "false")
    private Boolean isDefault;

    @Schema(description = "Set as billing default", example = "false")
    private Boolean isBillingDefault;

    @Schema(description = "Set as shipping default", example = "false")
    private Boolean isShippingDefault;
}