package usac.cunoc.bpmn.dto.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Basic article information DTO - used in order items and other references
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Basic article information")
public class ArticleBasicDto {

    @Schema(description = "Article ID", example = "1")
    private Integer id;

    @Schema(description = "Article title", example = "Abbey Road")
    private String title;

    @Schema(description = "Artist name", example = "The Beatles")
    private String artist;

    @Schema(description = "Article type", example = "vinyl")
    private String type;

    @Schema(description = "Image URL", example = "https://example.com/images/abbey-road.jpg")
    private String imageUrl;
}