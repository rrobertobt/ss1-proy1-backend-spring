package usac.cunoc.bpmn.dto.fileupload;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO containing presigned S3 upload URL and upload instructions
 * Provides frontend with temporary secure upload credentials
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response with presigned upload URL and instructions")
public class UploadUrlResponseDto {

    @Schema(description = "Presigned URL for file upload", example = "https://bpmn-archivos-semi1-g1.s3.us-east-2.amazonaws.com/Fotos/abbey-road-cover_1234567890.jpg?X-Amz-Algorithm=...", required = true)
    private String upload_url;

    @Schema(description = "Unique file key/path in S3 bucket", example = "Fotos/abbey-road-cover_1234567890.jpg", required = true)
    private String file_key;

    @Schema(description = "Original filename provided in request", example = "abbey-road-cover.jpg", required = true)
    private String original_filename;

    @Schema(description = "Final filename that will be stored in S3", example = "abbey-road-cover_1234567890.jpg", required = true)
    private String final_filename;

    @Schema(description = "Content type for the upload", example = "image/jpeg", required = true)
    private String content_type;

    @Schema(description = "Maximum file size allowed in bytes", example = "10485760", required = true)
    private Long max_file_size;

    @Schema(description = "Upload URL expiration time", example = "2024-01-15T10:35:00", required = true, type = "string", format = "date-time")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expires_at;

    @Schema(description = "Upload instructions for the frontend")
    private UploadInstructionsDto instructions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Instructions for performing the upload")
    public static class UploadInstructionsDto {

        @Schema(description = "HTTP method to use for upload", example = "PUT", required = true)
        private String method = "PUT";

        @Schema(description = "Required headers for the upload request")
        private UploadHeadersDto headers;

        @Schema(description = "Upload process description", example = "Use PUT request with file as binary body. Include Content-Type header.", required = true)
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Required headers for upload request")
    public static class UploadHeadersDto {

        @Schema(description = "Content-Type header value", example = "image/jpeg", required = true)
        private String content_type;

        @Schema(description = "Additional notes about headers", example = "Content-Type must match the type specified in the upload request")
        private String notes;
    }
}