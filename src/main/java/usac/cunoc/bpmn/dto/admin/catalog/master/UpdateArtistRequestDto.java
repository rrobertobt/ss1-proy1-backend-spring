package usac.cunoc.bpmn.dto.admin.catalog.master;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * Update artist request DTO - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update an artist")
public class UpdateArtistRequestDto {

    @NotBlank(message = "Artist name is required")
    @Size(max = 255, message = "Artist name cannot exceed 255 characters")
    @Schema(description = "Artist name", example = "The Beatles", required = true)
    private String name;

    @Schema(description = "Artist biography", example = "English rock band formed in Liverpool in 1960")
    private String biography;

    @Schema(description = "Formation date", example = "1960-08-01", type = "string", format = "date")
    private LocalDate formationDate;

    @Schema(description = "Career start date", example = "1960-08-01", type = "string", format = "date")
    private LocalDate careerStartDate;

    @NotNull(message = "Country ID is required")
    @Schema(description = "Country ID", example = "5", required = true)
    private Integer countryId;

    @NotNull(message = "Is band field is required")
    @Schema(description = "Whether this is a band or solo artist", example = "true", required = true)
    private Boolean isBand;

    @Size(max = 255, message = "Website cannot exceed 255 characters")
    @Schema(description = "Artist website", example = "https://www.thebeatles.com")
    private String website;
}