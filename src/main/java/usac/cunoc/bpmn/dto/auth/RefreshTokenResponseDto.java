package usac.cunoc.bpmn.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for refresh token response - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Refresh token response data")
public class refreshTokenResponseDto {

    @Schema(description = "New JWT access token")
    private String access_token;

    @Schema(description = "New JWT refresh token")
    private String refresh_token;
}