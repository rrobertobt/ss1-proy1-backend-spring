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
        private String firstName;

        @Schema(description = "Last name", example = "Doe")
        private String lastName;

        @Schema(description = "User type information")
        private UserTypeDto userType;

        @Schema(description = "Whether account is active", example = "true")
        private Boolean isActive;

        @Schema(description = "Whether account is banned", example = "false")
        private Boolean isBanned;

        @Schema(description = "Whether email is verified", example = "true")
        private Boolean isVerified;

        @Schema(description = "Total amount spent", example = "1250.50")
        private BigDecimal totalSpent;

        @Schema(description = "Total number of orders", example = "15")
        private Integer totalOrders;

        @Schema(description = "Number of deleted comments", example = "0")
        private Integer deletedCommentsCount;

        @Schema(description = "Account creation date")
        private LocalDateTime createdAt;

        @Schema(description = "Last login date")
        private LocalDateTime lastLogin;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "User type information")
    public static class UserTypeDto {
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
        private Integer currentPage;

        @Schema(description = "Total number of pages", example = "10")
        private Integer totalPages;

        @Schema(description = "Total number of items", example = "95")
        private Integer totalItems;

        @Schema(description = "Items per page", example = "10")
        private Integer itemsPerPage;
    }
}