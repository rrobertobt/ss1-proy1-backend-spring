package usac.cunoc.bpmn.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for logout request
 */
@Data
@Schema(description = "Logout request data")
public class LogoutRequestDto {

    @NotBlank(message = "Refresh token is required")
    @Schema(description = "JWT refresh token to invalidate")
    private String refreshToken;
}