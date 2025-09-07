package usac.cunoc.bpmn.dto.catalog.master;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Artist list response DTO for master data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for artists master data")
public class ArtistListResponseDto {

    @Schema(description = "List of artists")
    private List<ArtistDto> artists;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Artist information")
    public static class ArtistDto {
        @Schema(description = "Artist ID", example = "1")
        private Integer id;

        @Schema(description = "Artist name", example = "The Beatles")
        private String name;

        @Schema(description = "Is this a band", example = "true")
        private Boolean isBand;

        @Schema(description = "Artist website", example = "https://www.thebeatles.com")
        private String website;
    }
}