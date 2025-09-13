package usac.cunoc.bpmn.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for event list response - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Event list response data")
public class EventListResponseDto {

    @Schema(description = "List of events")
    private List<EventDto> events;

    @Schema(description = "Pagination information")
    private PaginationDto pagination;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Event summary information")
    public static class EventDto {
        @Schema(description = "Event ID", example = "1")
        private Integer id;

        @Schema(description = "Event title", example = "Jazz Night")
        private String title;

        @Schema(description = "Event description", example = "An evening of classic jazz")
        private String description;

        @Schema(description = "Associated article information")
        private ArticleDto article;

        @Schema(description = "Event start date and time")
        private LocalDateTime start_datetime;

        @Schema(description = "Event end date and time")
        private LocalDateTime end_datetime;

        @Schema(description = "Audio duration in seconds", example = "3600")
        private Integer duration;

        @Schema(description = "Maximum number of participants", example = "100")
        private Integer max_participants;

        @Schema(description = "Current number of participants", example = "45")
        private Integer current_participants;

        @Schema(description = "Event status", example = "Programado")
        private String status;

        @Schema(description = "Whether chat is allowed", example = "true")
        private Boolean allow_chat;

        @Schema(description = "Whether current user is registered", example = "false")
        private Boolean is_registered;
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

        @Schema(description = "Artist name", example = "Miles Davis")
        private String artist;

        @Schema(description = "Article image URL")
        private String image_url;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Pagination information")
    public static class PaginationDto {
        @Schema(description = "Current page number", example = "1")
        private Integer current_page;

        @Schema(description = "Total number of pages", example = "5")
        private Integer total_pages;

        @Schema(description = "Total number of items", example = "48")
        private Integer total_items;

        @Schema(description = "Items per page", example = "10")
        private Integer items_per_page;
    }
}