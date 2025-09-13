package usac.cunoc.bpmn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import usac.cunoc.bpmn.config.AwsS3Properties;
import usac.cunoc.bpmn.dto.fileupload.ConfirmUploadRequestDto;
import usac.cunoc.bpmn.dto.fileupload.ConfirmUploadResponseDto;
import usac.cunoc.bpmn.dto.fileupload.UploadUrlRequestDto;
import usac.cunoc.bpmn.dto.fileupload.UploadUrlResponseDto;
import usac.cunoc.bpmn.entity.AnalogArticle;
import usac.cunoc.bpmn.enums.FileUploadType;
import usac.cunoc.bpmn.repository.AnalogArticleRepository;
import usac.cunoc.bpmn.service.S3UploadService;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Implementation of S3 upload service for BPMN file management
 * Provides secure file upload capabilities using AWS S3 presigned URLs
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class S3UploadServiceImpl implements S3UploadService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final AwsS3Properties awsS3Properties;
    private final AnalogArticleRepository analogArticleRepository;

    @Override
    public UploadUrlResponseDto generateUploadUrl(UploadUrlRequestDto request, Integer adminUserId) {
        log.info("Generating upload URL for file: {} by admin user: {}", request.getFilename(), adminUserId);

        try {
            // Validate file type
            FileUploadType uploadType = validateAndGetFileType(request.getContent_type());

            // Validate file size
            validateFileSize(request.getFile_size());

            // Validate article exists
            AnalogArticle article = validateArticleExists(request.getArticle_id());

            // Generate unique filename and file key
            String uniqueFilename = generateUniqueFilename(request.getFilename());
            String fileKey = uploadType.getS3Folder() + uniqueFilename;

            // Create presigned URL
            String presignedUrl = createPresignedUrl(fileKey, request.getContent_type());

            // Calculate expiration time
            LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(awsS3Properties.getPresignedUrlExpiration());

            // Build response
            UploadUrlResponseDto response = buildUploadUrlResponse(
                    presignedUrl, fileKey, request.getFilename(), uniqueFilename,
                    request.getContent_type(), expiresAt);

            log.info("Successfully generated upload URL for article ID: {} with file key: {}",
                    request.getArticle_id(), fileKey);

            return response;

        } catch (Exception e) {
            log.error("Error generating upload URL for file: {} - {}", request.getFilename(), e.getMessage(), e);
            throw new RuntimeException("Error generating upload URL: " + e.getMessage(), e);
        }
    }

    @Override
    public ConfirmUploadResponseDto confirmUpload(Integer articleId, ConfirmUploadRequestDto request,
            Integer adminUserId) {
        log.info("Confirming upload for article ID: {} with file key: {} by admin user: {}",
                articleId, request.getFile_key(), adminUserId);

        try {
            // Validate article exists
            AnalogArticle article = validateArticleExists(articleId);

            // Validate file exists in S3
            if (!validateFileExists(request.getFile_key())) {
                throw new RuntimeException("File not found in S3 bucket: " + request.getFile_key());
            }

            // Get file type and validate
            FileUploadType fileType = determineFileTypeFromKey(request.getFile_key());

            // Store previous image URL for response
            String previousImageUrl = article.getImageUrl();

            // Generate public URL and update article
            String publicUrl = getPublicUrl(request.getFile_key());
            article.setImageUrl(publicUrl);
            article.setUpdatedAt(LocalDateTime.now());

            // Save updated article
            AnalogArticle savedArticle = analogArticleRepository.save(article);

            // Build response
            ConfirmUploadResponseDto response = buildConfirmUploadResponse(
                    savedArticle, request.getFile_key(), publicUrl, fileType, previousImageUrl);

            log.info("Successfully confirmed upload for article ID: {} with new image URL: {}",
                    articleId, publicUrl);

            return response;

        } catch (Exception e) {
            log.error("Error confirming upload for article ID: {} - {}", articleId, e.getMessage(), e);
            throw new RuntimeException("Error confirming upload: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean validateFileExists(String fileKey) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(awsS3Properties.getBucketName())
                    .key(fileKey)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;

        } catch (NoSuchKeyException e) {
            log.warn("File not found in S3: {}", fileKey);
            return false;
        } catch (Exception e) {
            log.error("Error validating file existence: {} - {}", fileKey, e.getMessage());
            return false;
        }
    }

    @Override
    public String getPublicUrl(String fileKey) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                awsS3Properties.getBucketName(),
                awsS3Properties.getRegion(),
                fileKey);
    }

    @Override
    public String generateUniqueFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("Original filename cannot be null or empty");
        }

        // Extract file extension
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex > 0 && lastDotIndex < originalFilename.length() - 1) {
            extension = originalFilename.substring(lastDotIndex);
        }

        // Get filename without extension
        String nameWithoutExtension = lastDotIndex > 0 ? originalFilename.substring(0, lastDotIndex) : originalFilename;

        // Clean filename (remove special characters, replace spaces with hyphens)
        String cleanName = nameWithoutExtension
                .replaceAll("[^a-zA-Z0-9\\-_]", "-")
                .replaceAll("-+", "-")
                .toLowerCase();

        // Generate timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());

        return cleanName + "_" + timestamp + extension;
    }

    @Override
    public boolean deleteFile(String fileKey) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(awsS3Properties.getBucketName())
                    .key(fileKey)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("Successfully deleted file from S3: {}", fileKey);
            return true;

        } catch (Exception e) {
            log.error("Error deleting file from S3: {} - {}", fileKey, e.getMessage());
            return false;
        }
    }

    // Private helper methods

    private FileUploadType validateAndGetFileType(String contentType) {
        if (!FileUploadType.isContentTypeAllowed(contentType)) {
            throw new IllegalArgumentException(
                    String.format("Unsupported file type: %s. Allowed types: %s",
                            contentType, getSupportedContentTypes()));
        }
        return FileUploadType.fromContentType(contentType);
    }

    private void validateFileSize(Long fileSize) {
        if (fileSize > awsS3Properties.getMaxFileSize()) {
            throw new IllegalArgumentException(
                    String.format("File size %d exceeds maximum allowed size %d bytes",
                            fileSize, awsS3Properties.getMaxFileSize()));
        }
    }

    private AnalogArticle validateArticleExists(Integer articleId) {
        return analogArticleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found with ID: " + articleId));
    }

    private String createPresignedUrl(String fileKey, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(awsS3Properties.getBucketName())
                .key(fileKey)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(awsS3Properties.getPresignedUrlExpiration()))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        return presignedRequest.url().toString();
    }

    private UploadUrlResponseDto buildUploadUrlResponse(String presignedUrl, String fileKey,
            String originalFilename, String finalFilename,
            String contentType, LocalDateTime expiresAt) {
        UploadUrlResponseDto response = new UploadUrlResponseDto();
        response.setUpload_url(presignedUrl);
        response.setFile_key(fileKey);
        response.setOriginal_filename(originalFilename);
        response.setFinal_filename(finalFilename);
        response.setContent_type(contentType);
        response.setMax_file_size(awsS3Properties.getMaxFileSize());
        response.setExpires_at(expiresAt);

        // Build instructions
        UploadUrlResponseDto.UploadInstructionsDto instructions = new UploadUrlResponseDto.UploadInstructionsDto();
        instructions.setMethod("PUT");
        instructions.setDescription(
                "Use PUT request with file as binary body. Include Content-Type header matching the specified type.");

        UploadUrlResponseDto.UploadHeadersDto headers = new UploadUrlResponseDto.UploadHeadersDto();
        headers.setContent_type(contentType);
        headers.setNotes("Content-Type must exactly match the type specified in the upload request");

        instructions.setHeaders(headers);
        response.setInstructions(instructions);

        return response;
    }

    private ConfirmUploadResponseDto buildConfirmUploadResponse(AnalogArticle article, String fileKey,
            String publicUrl, FileUploadType fileType,
            String previousImageUrl) {
        ConfirmUploadResponseDto response = new ConfirmUploadResponseDto();
        response.setArticle_id(article.getId());
        response.setArticle_title(article.getTitle());
        response.setFile_url(publicUrl);
        response.setFile_key(fileKey);
        response.setFile_type(fileType.getCode());
        response.setPrevious_image_url(previousImageUrl);
        response.setUpdated_at(article.getUpdatedAt());
        response.setStatus("completed");

        return response;
    }

    private FileUploadType determineFileTypeFromKey(String fileKey) {
        if (fileKey.startsWith("Fotos/")) {
            return FileUploadType.IMAGE;
        } else if (fileKey.startsWith("Audios/")) {
            return FileUploadType.AUDIO;
        }
        return FileUploadType.IMAGE; // default
    }

    private String getSupportedContentTypes() {
        StringBuilder sb = new StringBuilder();
        for (FileUploadType type : FileUploadType.values()) {
            sb.append(type.getAllowedMimeTypesString()).append(", ");
        }
        return sb.toString().replaceAll(", $", "");
    }
}