package usac.cunoc.bpmn.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for event chat response - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Event chat response data")
public class EventChatResponseDto {

    @Schema(description = "Event ID", example = "1")
    private Integer event_id;

    @Schema(description = "Chat messages")
    private List<MessageDto> messages;

    @Schema(description = "Pagination information")
    private PaginationDto pagination;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Chat message information")
    public static class MessageDto {
        @Schema(description = "Message ID", example = "1")
        private Integer id;

        @Schema(description = "User who sent the message")
        private UserDto user;

        @Schema(description = "Message content", example = "Hello everyone!")
        private String message;

        @Schema(description = "Whether this is a system message", example = "false")
        private Boolean is_system_message;

        @Schema(description = "Message sent date and time")
        private LocalDateTime sent_at;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "User information")
    public static class UserDto {
        @Schema(description = "User ID", example = "2")
        private Integer id;

        @Schema(description = "Username", example = "johndoe")
        private String username;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Pagination information")
    public static class PaginationDto {
        @Schema(description = "Current page number", example = "1")
        private Integer current_page;

        @Schema(description = "Total number of pages", example = "3")
        private Integer total_pages;

        @Schema(description = "Total number of items", example = "25")
        private Integer total_items;

        @Schema(description = "Items per page", example = "10")
        private Integer items_per_page;
    }
}