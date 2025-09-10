package usac.cunoc.bpmn.dto.admin.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for report comment response - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Report comment response data")
public class ReportCommentResponseDto {

    @Schema(description = "Reported comment ID", example = "1")
    private Integer commentId;

    @Schema(description = "Timestamp when comment was reported")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime reportedAt;
}