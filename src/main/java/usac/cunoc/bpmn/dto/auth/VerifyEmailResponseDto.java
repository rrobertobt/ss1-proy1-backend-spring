package usac.cunoc.bpmn.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for email verification response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Email verification response data")
public class VerifyEmailResponseDto {

    @Schema(description = "Whether email has been verified", example = "true")
    private Boolean is_verified;
}