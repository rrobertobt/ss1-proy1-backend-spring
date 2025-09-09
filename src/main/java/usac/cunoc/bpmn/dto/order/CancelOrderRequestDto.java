package usac.cunoc.bpmn.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for canceling orders - matches PDF JSON structure exactly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for canceling an order")
public class CancelOrderRequestDto {

    @NotBlank(message = "Cancellation reason is required")
    @Schema(description = "Reason for cancellation", example = "Customer changed mind", required = true)
    private String reason;
}