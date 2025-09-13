package usac.cunoc.bpmn.dto.admin.catalog;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Create article response DTO - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for article creation")
public class CreateArticleResponseDto {

    @Schema(description = "Created article ID", example = "1")
    private Integer id;

    @Schema(description = "Article title", example = "Abbey Road")
    private String title;

    @Schema(description = "Article type", example = "vinyl")
    private String type;

    @Schema(description = "Creation timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created_at;
}