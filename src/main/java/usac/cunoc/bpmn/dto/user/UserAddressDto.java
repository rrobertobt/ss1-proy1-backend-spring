package usac.cunoc.bpmn.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User address information DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User address information")
public class UserAddressDto {

    @Schema(description = "Address ID", example = "1")
    private Integer id;

    @Schema(description = "Address line 1", example = "123 Main Street")
    private String address_line1;

    @Schema(description = "Address line 2", example = "Apt 4B")
    private String address_line2;

    @Schema(description = "City", example = "Guatemala City")
    private String city;

    @Schema(description = "State/Province", example = "Guatemala")
    private String state;

    @Schema(description = "Postal code", example = "01001")
    private String postal_code;

    @Schema(description = "Country name", example = "Guatemala")
    private String country;

    @Schema(description = "Is default address", example = "true")
    private Boolean is_default;

    @Schema(description = "Is default billing address", example = "true")
    private Boolean is_billing_default;

    @Schema(description = "Is default shipping address", example = "true")
    private Boolean is_shipping_default;
}