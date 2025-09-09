package usac.cunoc.bpmn.service;

import usac.cunoc.bpmn.dto.payment.ProcessPaymentRequestDto;
import usac.cunoc.bpmn.dto.payment.ProcessPaymentResponseDto;

/**
 * Payment service interface for payment processing operations
 */
public interface PaymentService {

    /**
     * Process payment for an order and generate invoice
     * 
     * @param request Payment processing request data
     * @param userId  Current authenticated user ID
     * @return Payment processing response with invoice details
     */
    ProcessPaymentResponseDto processPayment(ProcessPaymentRequestDto request, Integer userId);
}