package usac.cunoc.bpmn.dto.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

/**
 * DTO for registration - exact PDF JSON structure with BD constraints
 */
@Data
@Schema(description = "User registration request data")
public class RegisterRequestDto {

    @NotBlank(message = "Username is required")
    @Size(max = 100, message = "Username must not exceed 100 characters")
    @Schema(description = "Unique username", example = "johndoe123")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Schema(description = "User email address", example = "john.doe@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    @Schema(description = "User password", example = "SecurePass123!")
    private String password;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Schema(description = "User first name", example = "John")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Schema(description = "User last name", example = "Doe")
    private String lastName;

    @Min(value = 1, message = "Invalid gender ID")
    @Schema(description = "Gender ID (1=Masculino, 2=Femenino, 3=Otro)", example = "1")
    private Integer genderId;

    @Past(message = "Birth date must be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "User birth date", example = "1990-05-15", type = "string", format = "date")
    private LocalDate birthDate;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    @Schema(description = "User phone number", example = "+50212345678")
    private String phone;
}