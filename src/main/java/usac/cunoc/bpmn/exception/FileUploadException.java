package usac.cunoc.bpmn.exception;

/**
 * Custom exception for file upload operations
 * Provides specific error handling for S3 upload processes
 */
public class FileUploadException extends RuntimeException {

    private final String errorCode;
    private final String userMessage;

    public FileUploadException(String message) {
        super(message);
        this.errorCode = "FILE_UPLOAD_ERROR";
        this.userMessage = message;
    }

    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "FILE_UPLOAD_ERROR";
        this.userMessage = message;
    }

    public FileUploadException(String errorCode, String message, String userMessage) {
        super(message);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }

    public FileUploadException(String errorCode, String message, String userMessage, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getUserMessage() {
        return userMessage;
    }

    // Specific exception types for different upload scenarios

    public static class UnsupportedFileTypeException extends FileUploadException {
        public UnsupportedFileTypeException(String fileType, String supportedTypes) {
            super("UNSUPPORTED_FILE_TYPE",
                    "Unsupported file type: " + fileType,
                    "Tipo de archivo no soportado: " + fileType + ". Tipos permitidos: " + supportedTypes);
        }
    }

    public static class FileSizeExceededException extends FileUploadException {
        public FileSizeExceededException(long fileSize, long maxSize) {
            super("FILE_SIZE_EXCEEDED",
                    "File size " + fileSize + " exceeds maximum " + maxSize,
                    "Tamaño de archivo excede el límite permitido de " + (maxSize / 1024 / 1024) + "MB");
        }
    }

    public static class ArticleNotFoundException extends FileUploadException {
        public ArticleNotFoundException(Integer articleId) {
            super("ARTICLE_NOT_FOUND",
                    "Article not found with ID: " + articleId,
                    "Artículo no encontrado con ID: " + articleId);
        }
    }

    public static class S3UploadFailedException extends FileUploadException {
        public S3UploadFailedException(String reason) {
            super("S3_UPLOAD_FAILED",
                    "S3 upload failed: " + reason,
                    "Error en la subida del archivo: " + reason);
        }

        public S3UploadFailedException(String reason, Throwable cause) {
            super("S3_UPLOAD_FAILED",
                    "S3 upload failed: " + reason,
                    "Error en la subida del archivo: " + reason,
                    cause);
        }
    }

    public static class FileNotFoundInS3Exception extends FileUploadException {
        public FileNotFoundInS3Exception(String fileKey) {
            super("FILE_NOT_FOUND_S3",
                    "File not found in S3 bucket: " + fileKey,
                    "Archivo no encontrado en S3. Verifica que la subida fue exitosa.");
        }
    }
}