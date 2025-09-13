package usac.cunoc.bpmn.dto.admin.catalog.master;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Create artist response DTO - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response when creating a new artist")
public class CreateArtistResponseDto {

    @Schema(description = "Artist ID", example = "1")
    private Integer id;

    @Schema(description = "Artist name", example = "The Beatles")
    private String name;

    @Schema(description = "Artist biography")
    private String biography;

    @Schema(description = "Formation date", example = "1960-08-01", type = "string", format = "date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate formation_date;

    @Schema(description = "Career start date", example = "1960-08-01", type = "string", format = "date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate career_start_date;

    @Schema(description = "Country information")
    private CountryDto country;

    @Schema(description = "Is this a band", example = "true")
    private Boolean is_band;

    @Schema(description = "Artist website", example = "https://www.thebeatles.com")
    private String website;

    @Schema(description = "Creation timestamp", example = "2025-01-15T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created_at;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Country information")
    public static class CountryDto {
        @Schema(description = "Country ID", example = "5")
        private Integer id;

        @Schema(description = "Country name", example = "Reino Unido")
        private String name;
    }
}