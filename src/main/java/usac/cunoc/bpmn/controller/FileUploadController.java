package usac.cunoc.bpmn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import usac.cunoc.bpmn.dto.common.ApiResponseDto;
import usac.cunoc.bpmn.dto.fileupload.ConfirmUploadRequestDto;
import usac.cunoc.bpmn.dto.fileupload.ConfirmUploadResponseDto;
import usac.cunoc.bpmn.dto.fileupload.UploadUrlRequestDto;
import usac.cunoc.bpmn.dto.fileupload.UploadUrlResponseDto;
import usac.cunoc.bpmn.entity.User;
import usac.cunoc.bpmn.repository.UserRepository;
import usac.cunoc.bpmn.service.S3UploadService;

/**
 * Controller for AWS S3 file upload operations
 * Provides secure file upload capabilities for admin users to manage article
 * assets
 * Follows the proposed architecture: generate presigned URLs, then confirm
 * uploads
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
@Tag(name = "File Upload Management", description = "AWS S3 file upload operations for article assets")
@Slf4j
public class FileUploadController {

    private final S3UploadService s3UploadService;
    private final UserRepository userRepository;

    @PostMapping("/upload-url")
    @Operation(summary = "Generate presigned upload URL", description = "Generate a secure, temporary URL for direct file upload to AWS S3. "
            +
            "The URL expires in 5 minutes and is specific to the file type and size.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Upload URL generated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or unsupported file type"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required"),
            @ApiResponse(responseCode = "404", description = "Article not found"),
            @ApiResponse(responseCode = "413", description = "File size exceeds maximum allowed limit"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponseDto<UploadUrlResponseDto>> generateUploadUrl(
            @Valid @RequestBody UploadUrlRequestDto request,
            Authentication authentication) {

        log.info("Generating upload URL request for file: {} by user: {}",
                request.getFilename(), authentication.getName());

        try {
            Integer adminUserId = getCurrentUserId(authentication);
            UploadUrlResponseDto response = s3UploadService.generateUploadUrl(request, adminUserId);

            log.info("Successfully generated upload URL for article ID: {} with expiration: {}",
                    request.getArticle_id(), response.getExpires_at());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDto.success(
                            "URL de subida generada exitosamente. Utiliza PUT request para subir el archivo.",
                            response));

        } catch (IllegalArgumentException e) {
            log.warn("Invalid upload request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Solicitud inválida: " + e.getMessage()));

        } catch (RuntimeException e) {
            log.error("Error generating upload URL: {}", e.getMessage(), e);

            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.error("Artículo no encontrado"));
            }

            if (e.getMessage().contains("exceeds maximum")) {
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                        .body(ApiResponseDto.error("Tamaño de archivo excede el límite permitido"));
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Error interno del servidor"));
        }
    }

    @PatchMapping("/catalog/articles/{id}")
    @Operation(summary = "Confirm successful file upload", description = "Confirm that a file has been successfully uploaded to S3 and update the article record "
            +
            "with the new file URL. This completes the upload process.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Upload confirmed and article updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or file not found in S3"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required"),
            @ApiResponse(responseCode = "404", description = "Article not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponseDto<ConfirmUploadResponseDto>> confirmUpload(
            @Parameter(description = "Article ID to update", example = "123") @PathVariable Integer id,
            @Valid @RequestBody ConfirmUploadRequestDto request,
            Authentication authentication) {

        log.info("Confirming upload for article ID: {} with file key: {} by user: {}",
                id, request.getFile_key(), authentication.getName());

        try {
            Integer adminUserId = getCurrentUserId(authentication);
            ConfirmUploadResponseDto response = s3UploadService.confirmUpload(id, request, adminUserId);

            log.info("Successfully confirmed upload for article ID: {} with new file URL: {}",
                    id, response.getFile_url());

            return ResponseEntity.ok(
                    ApiResponseDto.success(
                            "Archivo subido exitosamente y artículo actualizado",
                            response));

        } catch (IllegalArgumentException e) {
            log.warn("Invalid confirm upload request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponseDto.error("Solicitud inválida: " + e.getMessage()));

        } catch (RuntimeException e) {
            log.error("Error confirming upload for article ID: {} - {}", id, e.getMessage(), e);

            if (e.getMessage().contains("not found")) {
                if (e.getMessage().contains("Article")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponseDto.error("Artículo no encontrado"));
                } else {
                    return ResponseEntity.badRequest()
                            .body(ApiResponseDto
                                    .error("Archivo no encontrado en S3. Verifica que la subida fue exitosa."));
                }
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Error interno del servidor"));
        }
    }

    @GetMapping("/upload/validate/{fileKey:.+}")
    @Operation(summary = "Validate file exists in S3", description = "Check if a file exists in the S3 bucket. Useful for troubleshooting uploads.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File validation result returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    public ResponseEntity<ApiResponseDto<FileValidationResponse>> validateFile(
            @Parameter(description = "S3 file key to validate", example = "Fotos/album-cover_1234567890.jpg") @PathVariable String fileKey) {

        log.info("Validating file existence for key: {}", fileKey);

        try {
            boolean exists = s3UploadService.validateFileExists(fileKey);
            String publicUrl = exists ? s3UploadService.getPublicUrl(fileKey) : null;

            FileValidationResponse response = new FileValidationResponse(fileKey, exists, publicUrl);

            String message = exists ? "Archivo encontrado en S3" : "Archivo no encontrado en S3";
            return ResponseEntity.ok(ApiResponseDto.success(message, response));

        } catch (Exception e) {
            log.error("Error validating file: {} - {}", fileKey, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Error validando archivo"));
        }
    }

    /**
     * Get current authenticated admin user ID
     */
    private Integer getCurrentUserId(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
        return user.getId();
    }

    /**
     * Response DTO for file validation endpoint
     */
    public static class FileValidationResponse {
        public final String fileKey;
        public final boolean exists;
        public final String publicUrl;

        public FileValidationResponse(String fileKey, boolean exists, String publicUrl) {
            this.fileKey = fileKey;
            this.exists = exists;
            this.publicUrl = publicUrl;
        }
    }
}