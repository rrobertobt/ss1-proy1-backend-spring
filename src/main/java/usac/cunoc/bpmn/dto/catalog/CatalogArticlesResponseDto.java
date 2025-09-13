package usac.cunoc.bpmn.dto.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usac.cunoc.bpmn.dto.common.PaginationDto;
import java.math.BigDecimal;
import java.util.List;

/**
 * Catalog articles response DTO - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for catalog articles list")
public class CatalogArticlesResponseDto {

    @Schema(description = "List of articles")
    private List<CatalogArticleDto> articles;

    @Schema(description = "Pagination information")
    private PaginationDto pagination;

    @Schema(description = "Available filters")
    private FiltersDto filters;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Available filters for catalog")
    public static class FiltersDto {
        @Schema(description = "Available genres")
        private List<GenreFilterDto> genres;

        @Schema(description = "Available artists")
        private List<ArtistFilterDto> artists;

        @Schema(description = "Price range")
        private PriceRangeDto price_range;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Genre filter option")
    public static class GenreFilterDto {
        @Schema(description = "Genre ID", example = "1")
        private Integer id;

        @Schema(description = "Genre name", example = "Rock")
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Artist filter option")
    public static class ArtistFilterDto {
        @Schema(description = "Artist ID", example = "1")
        private Integer id;

        @Schema(description = "Artist name", example = "The Beatles")
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Price range information")
    public static class PriceRangeDto {
        @Schema(description = "Minimum price", example = "5.99")
        private BigDecimal min;

        @Schema(description = "Maximum price", example = "199.99")
        private BigDecimal max;
    }
}