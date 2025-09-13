package usac.cunoc.bpmn.dto.fileupload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for generating S3 presigned upload URL
 * Used by admin users to request upload permissions for article files
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for generating presigned upload URL")
public class UploadUrlRequestDto {

    @NotBlank(message = "Filename is required")
    @Schema(description = "Original filename with extension", example = "abbey-road-cover.jpg", maxLength = 255)
    private String filename;

    @NotBlank(message = "Content type is required")
    @Schema(description = "MIME type of the file", example = "image/jpeg", allowableValues = {
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp",
            "audio/mpeg", "audio/mp3", "audio/wav", "audio/ogg", "audio/m4a"
    })
    private String content_type;

    @NotNull(message = "File size is required")
    @Positive(message = "File size must be positive")
    @Schema(description = "File size in bytes", example = "2048576", minimum = "1", maximum = "10485760")
    private Long file_size;

    @NotNull(message = "Article ID is required")
    @Positive(message = "Article ID must be positive")
    @Schema(description = "ID of the article this file belongs to", example = "123")
    private Integer article_id;

    @Schema(description = "File purpose description", example = "Album cover image", maxLength = 500)
    private String description;
}