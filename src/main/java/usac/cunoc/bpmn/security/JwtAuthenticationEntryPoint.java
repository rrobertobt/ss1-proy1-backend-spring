package usac.cunoc.bpmn.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import usac.cunoc.bpmn.dto.common.ErrorResponseDto;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * JWT authentication entry point for handling unauthorized access
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ErrorResponseDto errorResponse = new ErrorResponseDto();
        errorResponse.setSuccess(false);
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setPath(request.getRequestURI());

        ErrorResponseDto.ErrorDetail errorDetail = new ErrorResponseDto.ErrorDetail();
        errorDetail.setCode("UNAUTHORIZED");
        errorDetail.setMessage("Access denied - Invalid or missing authentication token");
        errorDetail.setDetails(authException.getMessage());

        errorResponse.setError(errorDetail);

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }
}