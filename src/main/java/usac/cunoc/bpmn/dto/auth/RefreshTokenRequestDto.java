package usac.cunoc.bpmn.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for refresh token request - matches PDF specification exactly
 */
@Data
@Schema(description = "Refresh token request data")
public class RefreshTokenRequestDto {

    @NotBlank(message = "Refresh token is required")
    @Schema(description = "JWT refresh token")
    private String refresh_token;
}