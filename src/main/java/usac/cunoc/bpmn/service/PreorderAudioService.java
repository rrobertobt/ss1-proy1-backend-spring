package usac.cunoc.bpmn.service;

import usac.cunoc.bpmn.dto.preorder.PreorderAudioAccessResponseDto;
import usac.cunoc.bpmn.dto.preorder.PreorderAudioDownloadResponseDto;
import usac.cunoc.bpmn.dto.preorder.PreorderAudioPlayResponseDto;

/**
 * Service interface for preorder audio operations
 * Handles user access to preorder audio files for preview and download
 */
public interface PreorderAudioService {

    /**
     * Get user's accessible preorder audios
     * 
     * @param userId Current user ID
     * @return List of accessible audios with details
     */
    PreorderAudioAccessResponseDto getUserAccessibleAudios(Integer userId);

    /**
     * Register audio play and update statistics
     * 
     * @param audioId Preorder audio ID
     * @param userId  Current user ID
     * @return Play statistics response
     */
    PreorderAudioPlayResponseDto registerAudioPlay(Integer audioId, Integer userId);

    /**
     * Generate download link and mark as downloaded
     * 
     * @param audioId Preorder audio ID
     * @param userId  Current user ID
     * @return Download response with temporary URL
     */
    PreorderAudioDownloadResponseDto generateDownloadLink(Integer audioId, Integer userId);
}