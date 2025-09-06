package usac.cunoc.bpmn.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

/**
 * DTO for user registration request with database constraints validation
 */
@Data
@Schema(description = "User registration request data")
public class RegisterRequestDto {

    @NotBlank(message = "El nombre de usuario es requerido")
    @Size(min = 3, max = 100, message = "El nombre de usuario debe tener entre 3 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "El nombre de usuario solo puede contener letras, números, guiones y guiones bajos")
    @Schema(description = "Unique username for the user", example = "johndoe123")
    private String username;

    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 255, message = "El email no debe exceder 255 caracteres")
    @Schema(description = "User email address", example = "john.doe@example.com")
    private String email;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 8, max = 255, message = "La contraseña debe tener entre 8 y 255 caracteres")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", message = "La contraseña debe contener al menos una letra minúscula, una mayúscula, un número y un carácter especial")
    @Schema(description = "User password", example = "SecurePass123!")
    private String password;

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 100, message = "El nombre no debe exceder 100 caracteres")
    @Schema(description = "User first name", example = "John")
    private String firstName;

    @NotBlank(message = "El apellido es requerido")
    @Size(max = 100, message = "El apellido no debe exceder 100 caracteres")
    @Schema(description = "User last name", example = "Doe")
    private String lastName;

    @Min(value = 1, message = "ID de género inválido")
    @Max(value = 3, message = "ID de género inválido")
    @Schema(description = "User gender ID (1=Masculino, 2=Femenino, 3=Otro)", example = "1")
    private Integer genderId;

    @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
    @Schema(description = "User birth date", example = "1990-05-15")
    private LocalDate birthDate;

    @Size(max = 20, message = "El teléfono no debe exceder 20 caracteres")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]+$", message = "Formato de teléfono inválido")
    @Schema(description = "User phone number", example = "+50212345678")
    private String phone;
}