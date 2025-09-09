package usac.cunoc.bpmn.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Common status DTO - reusable across all responses for status information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Status information")
public class StatusDto {

    @Schema(description = "Status ID", example = "1")
    private Integer id;

    @Schema(description = "Status name", example = "Pendiente")
    private String name;
}