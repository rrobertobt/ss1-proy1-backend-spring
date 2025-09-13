package usac.cunoc.bpmn.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for updating user profile - matches PDF specification exactly
 */
@Data
@Schema(description = "Update user profile request data")
public class UpdateUserProfileRequestDto {

    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Schema(description = "First name", example = "John")
    private String first_name;

    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Schema(description = "Last name", example = "Doe")
    private String last_name;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    @Schema(description = "Phone number", example = "+50212345678")
    private String phone;

    @Schema(description = "Gender ID", example = "1")
    private Integer gender_id;
}