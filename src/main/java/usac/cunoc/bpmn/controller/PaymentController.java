package usac.cunoc.bpmn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import usac.cunoc.bpmn.dto.common.ApiResponseDto;
import usac.cunoc.bpmn.dto.payment.ProcessPaymentRequestDto;
import usac.cunoc.bpmn.dto.payment.ProcessPaymentResponseDto;
import usac.cunoc.bpmn.entity.User;
import usac.cunoc.bpmn.repository.UserRepository;
import usac.cunoc.bpmn.service.PaymentService;

/**
 * Payment controller for payment processing operations
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment processing operations")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;

    @PostMapping("/process")
    @Operation(summary = "Process payment", description = "Process payment for an order and generate invoice")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payment data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Order or payment method not found"),
            @ApiResponse(responseCode = "409", description = "Payment already processed")
    })
    public ResponseEntity<ApiResponseDto<ProcessPaymentResponseDto>> processPayment(
            @Valid @RequestBody ProcessPaymentRequestDto request,
            Authentication authentication) {

        Integer userId = getCurrentUserId(authentication);
        ProcessPaymentResponseDto response = paymentService.processPayment(request, userId);

        return ResponseEntity.ok(ApiResponseDto.success("Pago procesado exitosamente", response));
    }

    /**
     * Extract user ID from authentication
     */
    private Integer getCurrentUserId(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getId();
    }
}