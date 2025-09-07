package usac.cunoc.bpmn.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for user profile response - matches PDF specification exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User profile response data")
public class UserProfileResponseDto {

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

    @Schema(description = "Gender information")
    private GenderDto gender;

    @Schema(description = "Birth date", example = "1990-05-15")
    private LocalDate birthDate;

    @Schema(description = "Phone number", example = "+50212345678")
    private String phone;

    @Schema(description = "User type information")
    private UserTypeDto userType;

    @Schema(description = "Whether account is active", example = "true")
    private Boolean isActive;

    @Schema(description = "Whether email is verified", example = "true")
    private Boolean isVerified;

    @Schema(description = "Whether 2FA is enabled", example = "false")
    private Boolean is2faEnabled;

    @Schema(description = "Total amount spent", example = "1250.50")
    private BigDecimal totalSpent;

    @Schema(description = "Total number of orders", example = "15")
    private Integer totalOrders;

    @Schema(description = "Number of deleted comments", example = "0")
    private Integer deletedCommentsCount;

    @Schema(description = "Account creation date")
    private LocalDateTime createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Gender information")
    public static class GenderDto {
        @Schema(description = "Gender ID", example = "1")
        private Integer id;

        @Schema(description = "Gender name", example = "Masculino")
        private String name;
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
}