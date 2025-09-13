package usac.cunoc.bpmn.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration response - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User registration response data")
public class RegisterResponseDto {

    @Schema(description = "Created user ID", example = "1")
    private Integer user_id;

    @Schema(description = "User email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Whether email verification is required", example = "true")
    private Boolean verification_required;
}