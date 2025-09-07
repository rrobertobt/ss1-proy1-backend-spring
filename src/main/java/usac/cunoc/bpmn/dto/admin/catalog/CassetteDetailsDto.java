package usac.cunoc.bpmn.dto.admin.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cassette-specific details DTO for article creation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Cassette-specific details")
public class CassetteDetailsDto {

    @Schema(description = "Cassette category ID", example = "1")
    private Integer cassetteCategoryId;

    @Schema(description = "Cassette brand", example = "TDK")
    private String brand;

    @Schema(description = "Is chrome tape", example = "false")
    private Boolean isChromeTape = false;
}