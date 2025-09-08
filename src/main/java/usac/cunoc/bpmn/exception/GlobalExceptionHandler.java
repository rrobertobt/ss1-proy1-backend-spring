package usac.cunoc.bpmn.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import usac.cunoc.bpmn.dto.common.ErrorResponseDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for API errors
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }

        FieldError fieldError = ex.getBindingResult().getFieldError();
        String field = fieldError != null ? fieldError.getField() : "unknown";
        String message = fieldError != null ? fieldError.getDefaultMessage() : "Validation error";

        ErrorResponseDto.ErrorDetail errorDetail = new ErrorResponseDto.ErrorDetail();
        errorDetail.setCode("VALIDATION_ERROR");
        errorDetail.setMessage(message);
        errorDetail.setField(field);
        errorDetail.setDetails(String.join(", ", errors));

        ErrorResponseDto errorResponse = new ErrorResponseDto();
        errorResponse.setSuccess(false);
        errorResponse.setError(errorDetail);
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {

        log.error("Resource not found: {}", ex.getMessage());

        ErrorResponseDto.ErrorDetail errorDetail = new ErrorResponseDto.ErrorDetail();
        errorDetail.setCode("RESOURCE_NOT_FOUND");
        errorDetail.setMessage(ex.getMessage());

        ErrorResponseDto errorResponse = new ErrorResponseDto();
        errorResponse.setSuccess(false);
        errorResponse.setError(errorDetail);
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateResource(
            DuplicateResourceException ex, WebRequest request) {

        log.error("Duplicate resource: {}", ex.getMessage());

        ErrorResponseDto.ErrorDetail errorDetail = new ErrorResponseDto.ErrorDetail();
        errorDetail.setCode("DUPLICATE_RESOURCE");
        errorDetail.setMessage(ex.getMessage());

        ErrorResponseDto errorResponse = new ErrorResponseDto();
        errorResponse.setSuccess(false);
        errorResponse.setError(errorDetail);
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {

        log.error("Illegal argument: {}", ex.getMessage());

        ErrorResponseDto.ErrorDetail errorDetail = new ErrorResponseDto.ErrorDetail();
        errorDetail.setCode("INVALID_ARGUMENT");
        errorDetail.setMessage(ex.getMessage());

        ErrorResponseDto errorResponse = new ErrorResponseDto();
        errorResponse.setSuccess(false);
        errorResponse.setError(errorDetail);
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDenied(
            AccessDeniedException ex, WebRequest request) {

        log.error("Access denied: {}", ex.getMessage());

        ErrorResponseDto.ErrorDetail errorDetail = new ErrorResponseDto.ErrorDetail();
        errorDetail.setCode("ACCESS_DENIED");
        errorDetail.setMessage("Access denied. Insufficient permissions.");

        ErrorResponseDto errorResponse = new ErrorResponseDto();
        errorResponse.setSuccess(false);
        errorResponse.setError(errorDetail);
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleBadCredentials(
            BadCredentialsException ex, WebRequest request) {

        ErrorResponseDto.ErrorDetail errorDetail = new ErrorResponseDto.ErrorDetail();
        errorDetail.setCode("INVALID_CREDENTIALS");
        errorDetail.setMessage("Invalid username or password");

        ErrorResponseDto errorResponse = new ErrorResponseDto();
        errorResponse.setSuccess(false);
        errorResponse.setError(errorDetail);
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDto> handleRuntimeException(
            RuntimeException ex, WebRequest request) {

        log.error("Runtime exception occurred: ", ex);

        ErrorResponseDto.ErrorDetail errorDetail = new ErrorResponseDto.ErrorDetail();
        errorDetail.setCode("BUSINESS_ERROR");
        errorDetail.setMessage(ex.getMessage());

        ErrorResponseDto errorResponse = new ErrorResponseDto();
        errorResponse.setSuccess(false);
        errorResponse.setError(errorDetail);
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception ex, WebRequest request) {

        log.error("Unexpected error occurred: ", ex);

        ErrorResponseDto.ErrorDetail errorDetail = new ErrorResponseDto.ErrorDetail();
        errorDetail.setCode("INTERNAL_ERROR");
        errorDetail.setMessage("An unexpected error occurred");
        errorDetail.setDetails(ex.getMessage());

        ErrorResponseDto errorResponse = new ErrorResponseDto();
        errorResponse.setSuccess(false);
        errorResponse.setError(errorDetail);
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}