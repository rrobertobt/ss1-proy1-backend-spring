package usac.cunoc.bpmn.dto.catalog.master;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Genre list response DTO for master data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for genres master data")
public class GenreListResponseDto {

    @Schema(description = "List of music genres")
    private List<GenreDto> genres;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Genre information")
    public static class GenreDto {
        @Schema(description = "Genre ID", example = "1")
        private Integer id;

        @Schema(description = "Genre name", example = "Rock")
        private String name;

        @Schema(description = "Genre description")
        private String description;
    }
}