package usac.cunoc.bpmn.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for admin user list response - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Admin user list response data")
public class AdminUserListResponseDto {

    @Schema(description = "List of users")
    private List<UserSummaryDto> users;

    @Schema(description = "Pagination information")
    private PaginationDto pagination;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "User summary information")
    public static class UserSummaryDto {
        @Schema(description = "User ID", example = "1")
        private Integer id;

        @Schema(description = "Username", example = "johndoe123")
        private String username;

        @Schema(description = "Email address", example = "john.doe@example.com")
        private String email;

        @Schema(description = "First name", example = "John")
        private String first_name;

        @Schema(description = "Last name", example = "Doe")
        private String last_name;

        @Schema(description = "User type information")
        private user_typeDto user_type;

        @Schema(description = "Whether account is active", example = "true")
        private Boolean is_active;

        @Schema(description = "Whether account is banned", example = "false")
        private Boolean is_banned;

        @Schema(description = "Whether email is verified", example = "true")
        private Boolean is_verified;

        @Schema(description = "Total amount spent", example = "1250.50")
        private BigDecimal total_spent;

        @Schema(description = "Total number of orders", example = "15")
        private Integer total_orders;

        @Schema(description = "Number of deleted comments", example = "0")
        private Integer deleted_comments_count;

        @Schema(description = "Account creation date")
        private LocalDateTime created_at;

        @Schema(description = "Last login date")
        private LocalDateTime last_login;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "User type information")
    public static class user_typeDto {
        @Schema(description = "User type ID", example = "1")
        private Integer id;

        @Schema(description = "User type name", example = "Cliente")
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Pagination information")
    public static class PaginationDto {
        @Schema(description = "Current page number", example = "1")
        private Integer current_page;

        @Schema(description = "Total number of pages", example = "10")
        private Integer total_pages;

        @Schema(description = "Total number of items", example = "95")
        private Integer total_items;

        @Schema(description = "Items per page", example = "10")
        private Integer items_per_page;
    }
}