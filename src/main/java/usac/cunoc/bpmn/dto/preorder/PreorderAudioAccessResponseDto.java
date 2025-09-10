package usac.cunoc.bpmn.dto.preorder;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for user's accessible preorder audios
 * Matches PDF specification exactly for GET /api/v1/preorder-audios/my-access
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User's accessible preorder audios response")
public class PreorderAudioAccessResponseDto {

    @Schema(description = "List of accessible audios")
    private List<AccessibleAudioDto> accessibleAudios;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Accessible audio information")
    public static class AccessibleAudioDto {

        @Schema(description = "Audio ID", example = "1")
        private Integer id;

        @Schema(description = "Article information")
        private ArticleDto article;

        @Schema(description = "Track title", example = "Preview Track 1")
        private String trackTitle;

        @Schema(description = "Duration in seconds", example = "180")
        private Integer duration;

        @Schema(description = "File size in bytes", example = "5242880")
        private Integer fileSize;

        @Schema(description = "Is downloadable", example = "true")
        private Boolean isDownloadable;

        @Schema(description = "Access granted date")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime accessGrantedAt;

        @Schema(description = "Last played date")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime lastPlayedAt;

        @Schema(description = "Play count", example = "5")
        private Integer playCount;

        @Schema(description = "Is downloaded", example = "false")
        private Boolean downloaded;

        @Schema(description = "Downloaded date")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime downloadedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Article information")
    public static class ArticleDto {

        @Schema(description = "Article ID", example = "1")
        private Integer id;

        @Schema(description = "Article title", example = "Album Title")
        private String title;

        @Schema(description = "Artist name", example = "Artist Name")
        private String artist;

        @Schema(description = "Image URL", example = "https://example.com/image.jpg")
        private String imageUrl;
    }
}