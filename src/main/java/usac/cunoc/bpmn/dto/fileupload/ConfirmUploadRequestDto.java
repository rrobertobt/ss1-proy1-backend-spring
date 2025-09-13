package usac.cunoc.bpmn.dto.fileupload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for confirming successful file upload to S3
 * Used to update article record with final file URL
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to confirm successful file upload")
public class ConfirmUploadRequestDto {

    @NotBlank(message = "File key is required")
    @Schema(description = "S3 file key returned from upload URL generation", example = "Fotos/abbey-road-cover_1234567890.jpg", required = true)
    private String file_key;

    @Schema(description = "Upload success confirmation", example = "true", required = true)
    private Boolean upload_success = true;

    @Schema(description = "Upload completion notes", example = "File uploaded successfully", maxLength = 500)
    private String notes;
}