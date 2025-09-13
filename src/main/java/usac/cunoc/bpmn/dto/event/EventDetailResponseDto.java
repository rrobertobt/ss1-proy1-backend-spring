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
    private String audio_file_url;

    @Schema(description = "Audio duration in seconds", example = "3600")
    private Integer audio_duration;

    @Schema(description = "Event start date and time")
    private LocalDateTime start_datetime;

    @Schema(description = "Event end date and time")
    private LocalDateTime end_datetime;

    @Schema(description = "Maximum number of participants", example = "100")
    private Integer max_participants;

    @Schema(description = "Current number of participants", example = "45")
    private Integer current_participants;

    @Schema(description = "Whether chat is allowed", example = "true")
    private Boolean allow_chat;

    @Schema(description = "User who created the event")
    private CreatedByDto created_by;

    @Schema(description = "Whether current user is registered", example = "false")
    private Boolean is_registered;

    @Schema(description = "List of registered participants")
    private List<ParticipantDto> registered_participants;

    @Schema(description = "Event creation date")
    private LocalDateTime created_at;

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
        private Boolean allows_registration;
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
        private String image_url;
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
        private Integer user_id;

        @Schema(description = "Username", example = "johndoe")
        private String username;

        @Schema(description = "Registration date and time")
        private LocalDateTime registered_at;
    }
}