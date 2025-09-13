package usac.cunoc.bpmn.dto.admin.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CD-specific details DTO for article creation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "CD-specific details")
public class CdDetailsDto {

    @Schema(description = "Number of discs", example = "1")
    private Integer discount = 1;

    @Schema(description = "Has bonus content", example = "false")
    private Boolean has_bonus_content = false;

    @Schema(description = "Is remastered version", example = "false")
    private Boolean is_remastered = false;
}