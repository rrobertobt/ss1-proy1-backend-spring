package usac.cunoc.bpmn.dto.fileupload;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for successful upload confirmation
 * Confirms article has been updated with new file URL
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response confirming successful file upload and article update")
public class ConfirmUploadResponseDto {

    @Schema(description = "Article ID that was updated", example = "123", required = true)
    private Integer article_id;

    @Schema(description = "Article title for confirmation", example = "Abbey Road", required = true)
    private String article_title;

    @Schema(description = "Public URL of the uploaded file", example = "https://bpmn-archivos-semi1-g1.s3.us-east-2.amazonaws.com/Fotos/abbey-road-cover_1234567890.jpg", required = true)
    private String file_url;

    @Schema(description = "S3 file key", example = "Fotos/abbey-road-cover_1234567890.jpg", required = true)
    private String file_key;

    @Schema(description = "File type that was uploaded", example = "image", allowableValues = { "image", "audio" })
    private String file_type;

    @Schema(description = "File size in bytes", example = "2048576")
    private Long file_size;

    @Schema(description = "Previous image URL (if any)", example = "https://example.com/old-image.jpg")
    private String previous_image_url;

    @Schema(description = "Timestamp when the article was updated", example = "2024-01-15T10:30:00", type = "string", format = "date-time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updated_at;

    @Schema(description = "Upload process status", example = "completed", required = true)
    private String status = "completed";
}