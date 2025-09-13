package usac.cunoc.bpmn.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

/**
 * AWS S3 configuration properties for file upload functionality
 * Binds AWS S3 configuration from application.properties
 */
@Data
@Component
@ConfigurationProperties(prefix = "aws.s3")
@Validated
public class AwsS3Properties {

    // Optional credentials - when null, DefaultCredentialsProvider will be used
    // (IAM Role)
    private String accessKeyId;

    private String secretAccessKey;

    @NotBlank(message = "AWS Region is required")
    private String region;

    @NotBlank(message = "S3 Bucket name is required")
    private String bucketName;

    @NotNull(message = "Presigned URL expiration is required")
    @Positive(message = "Presigned URL expiration must be positive")
    private Integer presignedUrlExpiration = 300; // 5 minutes default

    @NotNull(message = "Max file size is required")
    @Positive(message = "Max file size must be positive")
    private Long maxFileSize = 10485760L; // 10MB default

    @NotNull(message = "Allowed file types list is required")
    private List<String> allowedFileTypes;

    /**
     * Get photos folder path in S3 bucket
     */
    public String getPhotosFolder() {
        return "Fotos/";
    }

    /**
     * Get audios folder path in S3 bucket
     */
    public String getAudiosFolder() {
        return "Audios/";
    }

    /**
     * Check if file type is allowed
     */
    public boolean isFileTypeAllowed(String contentType) {
        if (contentType == null || allowedFileTypes == null) {
            return false;
        }
        return allowedFileTypes.contains(contentType.toLowerCase());
    }

    /**
     * Get folder based on content type
     */
    public String getFolderByContentType(String contentType) {
        if (contentType == null) {
            return getPhotosFolder(); // default to photos
        }

        if (contentType.startsWith("audio/")) {
            return getAudiosFolder();
        } else if (contentType.startsWith("image/")) {
            return getPhotosFolder();
        }

        return getPhotosFolder(); // default to photos
    }
}