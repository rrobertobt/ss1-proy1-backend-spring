package usac.cunoc.bpmn.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for address response - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Address response data")
public class AddressResponseDto {

    @Schema(description = "Address ID", example = "1")
    private Integer id;

    @Schema(description = "Primary address line", example = "123 Main Street")
    private String address_line1;

    @Schema(description = "Secondary address line", example = "Apt 4B")
    private String address_line2;

    @Schema(description = "City", example = "Guatemala City")
    private String city;

    @Schema(description = "State or province", example = "Guatemala")
    private String state;

    @Schema(description = "Postal code", example = "01001")
    private String postal_code;

    @Schema(description = "Country information")
    private CountryDto country;

    @Schema(description = "Is default address", example = "false")
    private Boolean is_default;

    @Schema(description = "Is billing default", example = "false")
    private Boolean is_billing_default;

    @Schema(description = "Is shipping default", example = "false")
    private Boolean is_shipping_default;

    @Schema(description = "Creation date")
    private LocalDateTime created_at;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Country information")
    public static class CountryDto {
        @Schema(description = "Country ID", example = "1")
        private Integer id;

        @Schema(description = "Country name", example = "Guatemala")
        private String name;
    }
}