package usac.cunoc.bpmn.service;

import usac.cunoc.bpmn.dto.fileupload.ConfirmUploadRequestDto;
import usac.cunoc.bpmn.dto.fileupload.ConfirmUploadResponseDto;
import usac.cunoc.bpmn.dto.fileupload.UploadUrlRequestDto;
import usac.cunoc.bpmn.dto.fileupload.UploadUrlResponseDto;

/**
 * Service interface for AWS S3 file upload operations
 * Handles presigned URL generation and upload confirmation for article files
 */
public interface S3UploadService {

    /**
     * Generate presigned URL for file upload to S3
     * 
     * @param request     Upload request containing file details
     * @param adminUserId ID of the admin user requesting the upload
     * @return Response with presigned URL and upload instructions
     * @throws RuntimeException if file type not supported or validation fails
     */
    UploadUrlResponseDto generateUploadUrl(UploadUrlRequestDto request, Integer adminUserId);

    /**
     * Confirm successful file upload and update article record
     * 
     * @param articleId   ID of the article to update
     * @param request     Confirmation request with file details
     * @param adminUserId ID of the admin user confirming the upload
     * @return Response confirming the update
     * @throws RuntimeException if article not found or file verification fails
     */
    ConfirmUploadResponseDto confirmUpload(Integer articleId, ConfirmUploadRequestDto request, Integer adminUserId);

    /**
     * Validate if file exists in S3 bucket
     * 
     * @param fileKey S3 file key to validate
     * @return true if file exists, false otherwise
     */
    boolean validateFileExists(String fileKey);

    /**
     * Get public URL for S3 file
     * 
     * @param fileKey S3 file key
     * @return Public URL for the file
     */
    String getPublicUrl(String fileKey);

    /**
     * Generate unique filename based on original filename and timestamp
     * 
     * @param originalFilename Original filename with extension
     * @return Unique filename with timestamp
     */
    String generateUniqueFilename(String originalFilename);

    /**
     * Delete file from S3 bucket (for cleanup if needed)
     * 
     * @param fileKey S3 file key to delete
     * @return true if deletion successful
     */
    boolean deleteFile(String fileKey);
}