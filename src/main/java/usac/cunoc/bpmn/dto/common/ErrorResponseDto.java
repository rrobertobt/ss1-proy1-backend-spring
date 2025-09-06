package usac.cunoc.bpmn.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Error response DTO for API errors
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error response structure")
public class ErrorResponseDto {

    @Schema(description = "Success indicator", example = "false")
    private Boolean success = false;

    @Schema(description = "Error details")
    private ErrorDetail error;

    @Schema(description = "Error timestamp")
    private LocalDateTime timestamp = LocalDateTime.now();

    @Schema(description = "Request path")
    private String path;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Error detail information")
    public static class ErrorDetail {
        @Schema(description = "Error code", example = "VALIDATION_ERROR")
        private String code;

        @Schema(description = "Error message", example = "Invalid input data")
        private String message;

        @Schema(description = "Additional error details")
        private String details;

        @Schema(description = "Field that caused the error")
        private String field;
    }
}