package usac.cunoc.bpmn.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * File upload type enumeration for BPMN file upload system
 * Defines supported file types and their corresponding S3 folders
 */
@Getter
@RequiredArgsConstructor
public enum FileUploadType {

    IMAGE("image", "Fotos/", "Article images",
            new String[] { "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp" }),

    AUDIO("audio", "Audios/", "Article audio files",
            new String[] { "audio/mpeg", "audio/mp3", "audio/wav", "audio/ogg", "audio/m4a" });

    private final String code;
    private final String s3Folder;
    private final String description;
    private final String[] allowedMimeTypes;

    /**
     * Get FileUploadType from content type
     */
    public static FileUploadType fromContentType(String contentType) {
        if (contentType == null || contentType.trim().isEmpty()) {
            return null;
        }

        String cleanContentType = contentType.toLowerCase().trim();

        for (FileUploadType type : FileUploadType.values()) {
            for (String allowedType : type.allowedMimeTypes) {
                if (cleanContentType.equals(allowedType.toLowerCase())) {
                    return type;
                }
            }
        }

        return null;
    }

    /**
     * Check if content type is allowed
     */
    public static boolean isContentTypeAllowed(String contentType) {
        return fromContentType(contentType) != null;
    }

    /**
     * Check if this type supports the given content type
     */
    public boolean supportsContentType(String contentType) {
        if (contentType == null) {
            return false;
        }

        String cleanContentType = contentType.toLowerCase().trim();
        for (String allowedType : this.allowedMimeTypes) {
            if (cleanContentType.equals(allowedType.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get all allowed MIME types as comma-separated string
     */
    public String getAllowedMimeTypesString() {
        return String.join(", ", allowedMimeTypes);
    }

    @Override
    public String toString() {
        return this.code;
    }
}