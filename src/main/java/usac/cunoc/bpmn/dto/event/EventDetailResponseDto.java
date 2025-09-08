package usac.cunoc.bpmn.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for event detail response - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Event detail response data")
public class EventDetailResponseDto {

    @Schema(description = "Event ID", example = "1")
    private Integer id;

    @Schema(description = "Event title", example = "Jazz Night")
    private String title;

    @Schema(description = "Event description", example = "An evening of classic jazz")
    private String description;

    @Schema(description = "Event status information")
    private StatusDto status;

    @Schema(description = "Associated article information")
    private ArticleDto article;

    @Schema(description = "Audio file URL")
    private String audioFileUrl;

    @Schema(description = "Audio duration in seconds", example = "3600")
    private Integer audioDuration;

    @Schema(description = "Event start date and time")
    private LocalDateTime startDatetime;

    @Schema(description = "Event end date and time")
    private LocalDateTime endDatetime;

    @Schema(description = "Maximum number of participants", example = "100")
    private Integer maxParticipants;

    @Schema(description = "Current number of participants", example = "45")
    private Integer currentParticipants;

    @Schema(description = "Whether chat is allowed", example = "true")
    private Boolean allowChat;

    @Schema(description = "User who created the event")
    private CreatedByDto createdBy;

    @Schema(description = "Whether current user is registered", example = "false")
    private Boolean isRegistered;

    @Schema(description = "List of registered participants")
    private List<ParticipantDto> registeredParticipants;

    @Schema(description = "Event creation date")
    private LocalDateTime createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Event status information")
    public static class StatusDto {
        @Schema(description = "Status ID", example = "1")
        private Integer id;

        @Schema(description = "Status name", example = "Programado")
        private String name;

        @Schema(description = "Whether registration is allowed", example = "true")
        private Boolean allowsRegistration;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Article information for event")
    public static class ArticleDto {
        @Schema(description = "Article ID", example = "1")
        private Integer id;

        @Schema(description = "Article title", example = "Kind of Blue")
        private String title;

        @Schema(description = "Artist information")
        private ArtistDto artist;

        @Schema(description = "Article image URL")
        private String imageUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Artist information")
    public static class ArtistDto {
        @Schema(description = "Artist ID", example = "1")
        private Integer id;

        @Schema(description = "Artist name", example = "Miles Davis")
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Event creator information")
    public static class CreatedByDto {
        @Schema(description = "User ID", example = "1")
        private Integer id;

        @Schema(description = "Username", example = "admin")
        private String username;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Registered participant information")
    public static class ParticipantDto {
        @Schema(description = "User ID", example = "2")
        private Integer userId;

        @Schema(description = "Username", example = "johndoe")
        private String username;

        @Schema(description = "Registration date and time")
        private LocalDateTime registeredAt;
    }
}