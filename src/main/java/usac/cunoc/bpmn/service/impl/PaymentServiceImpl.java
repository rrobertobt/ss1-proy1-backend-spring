package usac.cunoc.bpmn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usac.cunoc.bpmn.dto.catalog.ArticleBasicDto;
import usac.cunoc.bpmn.dto.common.CurrencyDto;
import usac.cunoc.bpmn.dto.order.OrderItemDto;
import usac.cunoc.bpmn.dto.payment.ProcessPaymentRequestDto;
import usac.cunoc.bpmn.dto.payment.ProcessPaymentResponseDto;
import usac.cunoc.bpmn.dto.user.UserAddressDto;
import usac.cunoc.bpmn.entity.*;
import usac.cunoc.bpmn.repository.*;
import usac.cunoc.bpmn.service.PaymentService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Payment service implementation for processing payments and generating
 * invoices
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final CreditCardRepository creditCardRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final OrderStatusRepository orderStatusRepository;
    // Repositories for article type detection
    private final VinylRepository vinylRepository;
    private final CassetteRepository cassetteRepository;
    private final CdRepository cdRepository;

    @Override
    @Transactional
    public ProcessPaymentResponseDto processPayment(ProcessPaymentRequestDto request, Integer userId) {
        log.info("Processing payment for order {} by user {}", request.getOrder_id(), userId);

        // Get and validate user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Get and validate order belongs to user
        Order order = orderRepository.findById(request.getOrder_id())
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("No tienes permisos para procesar el pago de esta orden");
        }

        // Validate order is in a payable status (Pendiente)
        if (!"Pendiente".equals(order.getOrderStatus().getName())) {
            throw new RuntimeException("Esta orden ya no puede ser procesada para pago");
        }

        // Validate payment method exists and is active
        PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPayment_method_id())
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

        if (!paymentMethod.getIsActive()) {
            throw new RuntimeException("El método de pago seleccionado no está disponible");
        }

        // Validate credit card belongs to user and is active
        @SuppressWarnings("unused")
        CreditCard creditCard = creditCardRepository.findByIdAndUserAndIsActiveTrue(request.getCard_id(), user)
                .orElseThrow(() -> new RuntimeException("Tarjeta de crédito no encontrada o no válida"));

        // Validate payment amount matches order total
        if (request.getAmount().compareTo(order.getTotalAmount()) != 0) {
            throw new RuntimeException("El monto del pago no coincide con el total de la orden");
        }

        // Check if payment already exists for this order
        List<Payment> existingPayments = paymentRepository.findPaymentsByOrderId(order.getId());
        if (!existingPayments.isEmpty()) {
            throw new RuntimeException("Ya existe un pago procesado para esta orden");
        }

        // Process the payment
        Payment payment = processOrderPayment(order, paymentMethod, request.getAmount());

        // Generate or update invoice
        Invoice invoice = generateInvoice(order);

        // Update order status to "Procesando"
        updateOrderStatus(order);

        // Build response DTO
        ProcessPaymentResponseDto response = buildPaymentResponse(order, invoice, payment);

        log.info("Payment processed successfully: {} for order: {}", payment.getPaymentNumber(),
                order.getOrderNumber());

        return response;
    }

    /**
     * Process the actual payment and create payment record
     */
    private Payment processOrderPayment(Order order, PaymentMethod paymentMethod, BigDecimal amount) {
        // Get "Completado" payment status (assuming it exists in the database)
        PaymentStatus completedStatus = paymentStatusRepository.findByName("Completado")
                .orElse(paymentStatusRepository.findByName("Procesado")
                        .orElse(paymentStatusRepository.findByName("Exitoso")
                                .orElseThrow(() -> new RuntimeException("Estado de pago completado no encontrado"))));

        // Create payment record
        Payment payment = new Payment();
        payment.setPaymentNumber(generatePaymentNumber());
        payment.setOrder(order);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus(completedStatus);
        payment.setCurrency(order.getCurrency());
        payment.setAmount(amount);
        payment.setTransactionReference("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setGatewayTransactionId("GATEWAY-" + System.currentTimeMillis());
        payment.setProcessedAt(LocalDateTime.now());
        payment.setRefundedAmount(BigDecimal.ZERO);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    /**
     * Generate or update invoice for the order
     */
    private Invoice generateInvoice(Order order) {
        // Check if invoice already exists
        Invoice existingInvoice = invoiceRepository.findByOrderId(order.getId()).orElse(null);

        if (existingInvoice != null) {
            return existingInvoice;
        }

        // Create new invoice
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setOrder(order);
        invoice.setCurrency(order.getCurrency());
        invoice.setIssueDate(LocalDate.now());
        invoice.setDueDate(LocalDate.now().plusDays(30)); // 30 days payment terms
        invoice.setTaxId(generateTaxId());
        invoice.setSubtotal(order.getSubtotal());
        invoice.setTaxAmount(order.getTaxAmount());
        invoice.setTotalAmount(order.getTotalAmount());
        invoice.setNotes("Factura generada automáticamente por procesamiento de pago");
        invoice.setPdfUrl(generatePdfUrl(invoice.getInvoiceNumber()));
        invoice.setCreatedAt(LocalDateTime.now());

        return invoiceRepository.save(invoice);
    }

    /**
     * Update order status to "Procesando"
     */
    private void updateOrderStatus(Order order) {
        // Get "Procesando" status
        OrderStatus processingStatus = orderStatusRepository.findByName("Procesando")
                .orElseThrow(() -> new RuntimeException("Estado 'Procesando' no encontrado"));

        order.setOrderStatus(processingStatus);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        log.info("Order status updated to 'Procesando' for order: {}", order.getOrderNumber());
    }

    /**
     * Build the response DTO with invoice and order details
     */
    private ProcessPaymentResponseDto buildPaymentResponse(Order order, Invoice invoice, Payment payment) {
        // Get order items
        List<OrderItem> orderItems = orderItemRepository.findOrderItemsWithDetailsByOrderId(order.getId());

        // Map order items to DTOs
        List<OrderItemDto> itemDtos = orderItems.stream()
                .map(this::mapToOrderItemDto)
                .collect(Collectors.toList());

        // Map customer info
        ProcessPaymentResponseDto.CustomerDto customer = new ProcessPaymentResponseDto.CustomerDto(
                order.getUser().getFirstName(),
                order.getUser().getLastName(),
                order.getUser().getEmail());

        // Map billing address
        UserAddressDto billingAddress = mapToUserAddressDto(order.getBillingAddress());

        // Map currency
        CurrencyDto currency = new CurrencyDto(
                order.getCurrency().getCode(),
                order.getCurrency().getSymbol());

        return new ProcessPaymentResponseDto(
                invoice.getInvoiceNumber(),
                order.getId(),
                order.getOrderNumber(),
                invoice.getIssueDate(),
                invoice.getDueDate(),
                invoice.getSubtotal(),
                invoice.getTaxAmount(),
                invoice.getTotalAmount(),
                currency,
                invoice.getPdfUrl(),
                customer,
                billingAddress,
                itemDtos);
    }

    /**
     * Map OrderItem to OrderItemDto
     */
    private OrderItemDto mapToOrderItemDto(OrderItem item) {
        // Create article basic info
        ArticleBasicDto article = new ArticleBasicDto(
                item.getAnalogArticle().getId(),
                item.getAnalogArticle().getTitle(),
                item.getAnalogArticle().getArtist().getName(),
                getArticleType(item.getAnalogArticle()),
                item.getAnalogArticle().getImageUrl());

        return new OrderItemDto(
                item.getId(),
                article,
                item.getQuantity(),
                item.getUnitPrice(),
                item.getDiscountAmount(),
                item.getTotalPrice(),
                item.getCdPromotion() != null ? item.getCdPromotion().getName() : null);
    }

    /**
     * Determine article type by checking specific tables
     */
    private String getArticleType(AnalogArticle article) {
        if (vinylRepository.existsByAnalogArticleId(article.getId())) {
            return "Vinilo";
        } else if (cassetteRepository.existsByAnalogArticleId(article.getId())) {
            return "Cassette";
        } else if (cdRepository.existsByAnalogArticleId(article.getId())) {
            return "CD";
        }
        return "Desconocido";
    }

    /**
     * Map UserAddress to UserAddressDto
     */
    private UserAddressDto mapToUserAddressDto(UserAddress address) {
        if (address == null) {
            return null;
        }

        return new UserAddressDto(
                address.getId(),
                address.getAddressLine1(),
                address.getAddressLine2(),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getCountry().getName(),
                address.getIsDefault(),
                address.getIsBillingDefault(),
                address.getIsShippingDefault());
    }

    /**
     * Generate unique payment number
     */
    private String generatePaymentNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "PAY-" + timestamp + "-" + randomPart;
    }

    /**
     * Generate unique invoice number
     */
    private String generateInvoiceNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "INV-" + timestamp + "-" + randomPart;
    }

    /**
     * Generate tax ID for invoice
     */
    private String generateTaxId() {
        return "TAX-" + System.currentTimeMillis();
    }

    /**
     * Generate PDF URL for invoice
     */
    private String generatePdfUrl(String invoiceNumber) {
        return "/api/v1/invoices/" + invoiceNumber + "/pdf";
    }
}