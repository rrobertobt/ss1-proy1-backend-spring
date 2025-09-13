package usac.cunoc.bpmn.dto.catalog;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usac.cunoc.bpmn.dto.common.PaginationDto;
import java.util.List;

/**
 * Article comments response DTO - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for article comments")
public class ArticleCommentsResponseDto {

    @Schema(description = "Article ID", example = "1")
    private Integer article_id;

    @Schema(description = "Total comments count", example = "45")
    private Integer total_comments;

    @Schema(description = "List of comments")
    private List<ArticleCommentDto> comments;

    @Schema(description = "Pagination information")
    private PaginationDto pagination;
}