package usac.cunoc.bpmn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usac.cunoc.bpmn.dto.preorder.PreorderAudioAccessResponseDto;
import usac.cunoc.bpmn.dto.preorder.PreorderAudioDownloadResponseDto;
import usac.cunoc.bpmn.dto.preorder.PreorderAudioPlayResponseDto;
import usac.cunoc.bpmn.entity.PreorderAudio;
import usac.cunoc.bpmn.entity.UserPreorderAudioAccess;
import usac.cunoc.bpmn.repository.PreorderAudioRepository;
import usac.cunoc.bpmn.repository.UserPreorderAudioAccessRepository;
import usac.cunoc.bpmn.service.PreorderAudioService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of PreorderAudioService - handles all preorder audio
 * operations
 * Follows business rules and complies with database constraints
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PreorderAudioServiceImpl implements PreorderAudioService {

    private final UserPreorderAudioAccessRepository userPreorderAudioAccessRepository;
    private final PreorderAudioRepository preorderAudioRepository;

    @Override
    @Transactional(readOnly = true)
    public PreorderAudioAccessResponseDto getUserAccessibleAudios(Integer userId) {
        log.info("Fetching accessible audios for user: {}", userId);

        // Get user's accessible audios with all details
        List<UserPreorderAudioAccess> accessList = userPreorderAudioAccessRepository
                .findAccessibleAudiosByUserId(userId);

        // Map to DTOs
        List<PreorderAudioAccessResponseDto.AccessibleAudioDto> accessibleAudios = accessList.stream()
                .map(this::mapToAccessibleAudioDto)
                .collect(Collectors.toList());

        log.info("Found {} accessible audios for user: {}", accessibleAudios.size(), userId);

        return new PreorderAudioAccessResponseDto(accessibleAudios);
    }

    @Override
    @Transactional
    public PreorderAudioPlayResponseDto registerAudioPlay(Integer audioId, Integer userId) {
        log.info("Registering audio play for audio: {} by user: {}", audioId, userId);

        // Validate user has access to this audio
        UserPreorderAudioAccess access = userPreorderAudioAccessRepository
                .findByUserIdAndAudioId(userId, audioId)
                .orElseThrow(() -> new RuntimeException("No tienes acceso a este audio"));

        // Update play statistics
        LocalDateTime playTime = LocalDateTime.now();
        userPreorderAudioAccessRepository.updatePlayStatistics(access.getId(), playTime);

        // Update local object for response
        access.setPlayCount(access.getPlayCount() + 1);
        access.setLastPlayedAt(playTime);

        log.info("Audio play registered successfully. New play count: {}", access.getPlayCount());

        return new PreorderAudioPlayResponseDto(
                audioId,
                access.getPlayCount(),
                playTime);
    }

    @Override
    @Transactional
    public PreorderAudioDownloadResponseDto generateDownloadLink(Integer audioId, Integer userId) {
        log.info("Generating download link for audio: {} by user: {}", audioId, userId);

        // Validate user has access to this audio
        UserPreorderAudioAccess access = userPreorderAudioAccessRepository
                .findByUserIdAndAudioId(userId, audioId)
                .orElseThrow(() -> new RuntimeException("No tienes acceso a este audio"));

        // Validate audio is downloadable
        PreorderAudio audio = access.getPreorderAudio();
        if (!Boolean.TRUE.equals(audio.getIsDownloadable())) {
            throw new RuntimeException("Este audio no está disponible para descarga");
        }

        // Generate temporary download URL (valid for 24 hours)
        String downloadUrl = generateTemporaryDownloadUrl(audio.getAudioFileUrl(), audioId);
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);
        LocalDateTime downloadTime = LocalDateTime.now();

        // Mark as downloaded if not already
        if (!Boolean.TRUE.equals(access.getDownloaded())) {
            userPreorderAudioAccessRepository.markAsDownloaded(access.getId(), downloadTime);

            // Update download count for audio
            preorderAudioRepository.incrementDownloadCount(audioId);
        }

        log.info("Download link generated successfully for audio: {}", audioId);

        return new PreorderAudioDownloadResponseDto(
                audioId,
                downloadUrl,
                expiresAt,
                true,
                downloadTime);
    }

    // PRIVATE HELPER METHODS

    /**
     * Map UserPreorderAudioAccess to AccessibleAudioDto
     */
    private PreorderAudioAccessResponseDto.AccessibleAudioDto mapToAccessibleAudioDto(
            UserPreorderAudioAccess access) {

        PreorderAudio audio = access.getPreorderAudio();

        // Map article info
        PreorderAudioAccessResponseDto.ArticleDto articleDto = null;
        if (audio.getAnalogArticle() != null) {
            articleDto = new PreorderAudioAccessResponseDto.ArticleDto(
                    audio.getAnalogArticle().getId(),
                    audio.getAnalogArticle().getTitle(),
                    audio.getAnalogArticle().getArtist() != null ? audio.getAnalogArticle().getArtist().getName()
                            : null,
                    audio.getAnalogArticle().getImageUrl());
        }

        // Convert file size from bytes to integer (with safe casting)
        Integer fileSize = audio.getFileSizeBytes() != null ? audio.getFileSizeBytes().intValue() : null;

        return new PreorderAudioAccessResponseDto.AccessibleAudioDto(
                audio.getId(),
                articleDto,
                audio.getTrackTitle(),
                audio.getDurationSeconds(),
                fileSize,
                audio.getIsDownloadable(),
                access.getAccessGrantedAt(),
                access.getLastPlayedAt(),
                access.getPlayCount(),
                access.getDownloaded(),
                access.getDownloadedAt());
    }

    /**
     * Generate temporary download URL
     * In production, this would integrate with a CDN or file service
     */
    private String generateTemporaryDownloadUrl(String baseUrl, Integer audioId) {
        // This is a simplified implementation
        // In production, you would integrate with a proper file service/CDN
        // that generates signed URLs with expiration
        String timestamp = String.valueOf(System.currentTimeMillis());
        return String.format("https://cdn.bpmn.com/downloads/audio_%d_%s.mp3", audioId, timestamp);
    }
}