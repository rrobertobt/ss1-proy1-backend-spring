package usac.cunoc.bpmn.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usac.cunoc.bpmn.dto.catalog.ArticleBasicDto;
import usac.cunoc.bpmn.dto.common.CurrencyDto;
import usac.cunoc.bpmn.dto.common.PaginationDto;
import usac.cunoc.bpmn.dto.common.StatusDto;
import usac.cunoc.bpmn.dto.order.*;
import usac.cunoc.bpmn.dto.user.UserAddressDto;
import usac.cunoc.bpmn.entity.*;
import usac.cunoc.bpmn.repository.*;
import usac.cunoc.bpmn.service.OrderService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Order service implementation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

        private final OrderRepository orderRepository;
        private final OrderItemRepository orderItemRepository;
        private final PaymentRepository paymentRepository;
        private final UserRepository userRepository;
        private final UserAddressRepository userAddressRepository;
        private final ShoppingCartRepository shoppingCartRepository;
        private final ShoppingCartItemRepository shoppingCartItemRepository;
        private final OrderStatusRepository orderStatusRepository;
        private final PaymentMethodRepository paymentMethodRepository;
        private final PaymentStatusRepository paymentStatusRepository;
        private final CurrencyRepository currencyRepository;
        private final AnalogArticleRepository analogArticleRepository;
        private final InvoiceRepository invoiceRepository;

        // Added repositories for article type detection - FIXED
        private final VinylRepository vinylRepository;
        private final CassetteRepository cassetteRepository;
        private final CdRepository cdRepository;

        @Override
        @Transactional
        public CreateOrderResponseDto createOrder(CreateOrderRequestDto request, Integer userId) {
                // Get user and validate existence
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                // Validate addresses belong to user
                UserAddress shippingAddress = userAddressRepository
                                .findByIdAndUser(request.getShipping_address_id(), user)
                                .orElseThrow(() -> new RuntimeException("Dirección de envío no válida"));

                UserAddress billingAddress = userAddressRepository
                                .findByIdAndUser(request.getBilling_address_id(), user)
                                .orElseThrow(() -> new RuntimeException("Dirección de facturación no válida"));

                // Get user's shopping cart
                ShoppingCart cart = shoppingCartRepository.findByUser(user)
                                .orElseThrow(() -> new RuntimeException("Carrito de compras no encontrado"));

                // Get cart items using correct method - FIXED
                List<ShoppingCartItem> cartItems = shoppingCartItemRepository.findByCartIdWithDetails(cart.getId());
                if (cartItems.isEmpty()) {
                        throw new RuntimeException("El carrito está vacío");
                }

                // Get order status (Pendiente = 1)
                OrderStatus pendingStatus = orderStatusRepository.findByName("Pendiente")
                                .orElseThrow(() -> new RuntimeException("Estado de orden 'Pendiente' no encontrado"));

                // Get currency (assuming GTQ is default currency with ID 1)
                Currency currency = currencyRepository.findById(1)
                                .orElseThrow(() -> new RuntimeException("Moneda no encontrada"));

                // Calculate totals
                BigDecimal subtotal = cartItems.stream()
                                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                                                .subtract(item.getDiscountApplied()))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal taxAmount = subtotal.multiply(BigDecimal.valueOf(0.12)); // 12% IVA
                BigDecimal shippingCost = BigDecimal.valueOf(25.00); // Fixed shipping cost
                BigDecimal totalAmount = subtotal.add(taxAmount).add(shippingCost);

                // Create order
                Order order = new Order();
                order.setOrderNumber(generateOrderNumber());
                order.setUser(user);
                order.setOrderStatus(pendingStatus);
                order.setCurrency(currency);
                order.setSubtotal(subtotal);
                order.setTaxAmount(taxAmount);
                order.setDiscountAmount(BigDecimal.ZERO);
                order.setShippingCost(shippingCost);
                order.setTotalAmount(totalAmount);
                order.setTotalItems(cartItems.stream().mapToInt(ShoppingCartItem::getQuantity).sum());
                order.setShippingAddress(shippingAddress);
                order.setBillingAddress(billingAddress);
                order.setNotes(request.getNotes());
                order.setCreatedAt(LocalDateTime.now());
                order.setUpdatedAt(LocalDateTime.now());

                Order savedOrder = orderRepository.save(order);

                // Create order items from cart items and use them for validation - FIXED
                // WARNING
                List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
                        OrderItem orderItem = new OrderItem();
                        orderItem.setOrder(savedOrder);
                        orderItem.setAnalogArticle(cartItem.getAnalogArticle());
                        orderItem.setQuantity(cartItem.getQuantity());
                        orderItem.setUnitPrice(cartItem.getUnitPrice());
                        orderItem.setDiscountAmount(cartItem.getDiscountApplied());
                        orderItem.setTotalPrice(
                                        cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))
                                                        .subtract(cartItem.getDiscountApplied()));
                        orderItem.setCdPromotion(cartItem.getCdPromotion());
                        orderItem.setCreatedAt(LocalDateTime.now());
                        return orderItem;
                }).collect(Collectors.toList());

                orderItemRepository.saveAll(orderItems);

                // Log order items for audit purposes - USING VARIABLE
                log.info("Created {} order items for order {}", orderItems.size(), savedOrder.getId());

                // Process payment if method provided - FIXED: No getPaymentDetails method issue
                if (request.getPayment_method_id() != null) {
                        processPayment(savedOrder, request);
                }

                // Clear shopping cart
                shoppingCartItemRepository.deleteAll(cartItems);
                cart.setTotalItems(0);
                cart.setSubtotal(BigDecimal.ZERO);
                cart.setUpdatedAt(LocalDateTime.now());
                shoppingCartRepository.save(cart);

                log.info("Order created successfully with ID: {} for user: {}", savedOrder.getId(), userId);

                return new CreateOrderResponseDto(
                                savedOrder.getId(),
                                savedOrder.getOrderNumber(),
                                new StatusDto(savedOrder.getOrderStatus().getId(),
                                                savedOrder.getOrderStatus().getName()),
                                savedOrder.getTotalAmount(),
                                new CurrencyDto(savedOrder.getCurrency().getCode(),
                                                savedOrder.getCurrency().getSymbol()),
                                savedOrder.getTotalItems(),
                                savedOrder.getCreatedAt());
        }

        @Override
        @Transactional(readOnly = true)
        public OrderListResponseDto getUserOrders(Integer page, Integer limit, String status, Integer userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                Pageable pageable = PageRequest.of(page - 1, limit);
                Page<Order> orderPage;

                // Use correct repository methods - FIXED
                if (status != null && !status.trim().isEmpty()) {
                        orderPage = orderRepository.findOrdersByUserAndStatus(user, status, pageable);
                } else {
                        orderPage = orderRepository.findOrdersByUser(user, pageable);
                }

                List<OrderSummaryDto> orderSummaries = orderPage.getContent().stream()
                                .map(this::mapToOrderSummary)
                                .collect(Collectors.toList());

                PaginationDto pagination = new PaginationDto(
                                orderPage.getNumber() + 1,
                                orderPage.getTotalPages(),
                                (int) orderPage.getTotalElements(),
                                orderPage.getSize());

                return new OrderListResponseDto(orderSummaries, pagination);
        }

        @Override
        @Transactional(readOnly = true)
        public OrderDetailResponseDto getOrderById(Integer orderId, Integer userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                // Use correct repository method - FIXED
                Order order = orderRepository.findOrderByIdAndUser(orderId, user)
                                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

                // Use correct repository methods - FIXED
                List<OrderItem> orderItems = orderItemRepository.findOrderItemsWithDetailsByOrderId(orderId);
                List<Payment> payments = paymentRepository.findPaymentsByOrderId(orderId);

                return mapToOrderDetailResponse(order, orderItems, payments);
        }

        @Override
        @Transactional
        public CancelOrderResponseDto cancelOrder(Integer orderId, CancelOrderRequestDto request, Integer userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                // Use correct repository method - FIXED
                Order order = orderRepository.findOrderByIdAndUser(orderId, user)
                                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

                // Validate order can be cancelled (only pending orders)
                if (!"Pendiente".equals(order.getOrderStatus().getName())) {
                        throw new RuntimeException("Solo se pueden cancelar órdenes pendientes");
                }

                // Get cancelled status
                OrderStatus cancelledStatus = orderStatusRepository.findByName("Cancelado")
                                .orElseThrow(() -> new RuntimeException("Estado 'Cancelado' no encontrado"));

                // Restore stock for cancelled items
                List<OrderItem> orderItems = orderItemRepository.findOrderItemsByOrderId(orderId);
                for (OrderItem item : orderItems) {
                        AnalogArticle article = item.getAnalogArticle();
                        article.setStockQuantity(article.getStockQuantity() + item.getQuantity());
                        analogArticleRepository.save(article);
                }

                order.setOrderStatus(cancelledStatus);
                order.setNotes((order.getNotes() != null ? order.getNotes() + "\n" : "") +
                                "Cancelado por: " + request.getReason());
                order.setUpdatedAt(LocalDateTime.now());

                Order savedOrder = orderRepository.save(order);

                log.info("Order cancelled successfully: {} by user: {}", orderId, userId);

                return new CancelOrderResponseDto(
                                savedOrder.getId(),
                                savedOrder.getOrderNumber(),
                                savedOrder.getOrderStatus().getName(),
                                savedOrder.getUpdatedAt());
        }

        @Override
        @Transactional(readOnly = true)
        public OrderInvoiceResponseDto getOrderInvoice(Integer orderId, Integer userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                Order order = orderRepository.findOrderByIdAndUser(orderId, user)
                                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

                // Find or create invoice - FIXED using correct method
                Optional<Invoice> existingInvoice = invoiceRepository.findByOrderId(orderId);

                if (existingInvoice.isPresent()) {
                        return mapToOrderInvoiceResponse(existingInvoice.get(), order);
                } else {
                        // Create invoice if it doesn't exist
                        Invoice invoice = createInvoice(order);
                        return mapToOrderInvoiceResponse(invoice, order);
                }
        }

        // PRIVATE HELPER METHODS

        /**
         * Generate unique order number
         */
        private String generateOrderNumber() {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                return "ORD-" + timestamp + "-" + randomPart;
        }

        /**
         * Process payment for the order - FIXED: Remove PaymentDetailsDto dependency
         */
        private void processPayment(Order order, CreateOrderRequestDto request) {
                // Get payment method
                PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPayment_method_id())
                                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

                // Get pending payment status
                PaymentStatus pendingStatus = paymentStatusRepository.findByName("Pendiente")
                                .orElseThrow(() -> new RuntimeException("Estado de pago 'Pendiente' no encontrado"));

                // Create payment record and use it for logging - FIXED WARNING
                Payment payment = new Payment();
                payment.setPaymentNumber(generatePaymentNumber());
                payment.setOrder(order);
                payment.setPaymentMethod(paymentMethod);
                payment.setPaymentStatus(pendingStatus);
                payment.setCurrency(order.getCurrency());
                payment.setAmount(order.getTotalAmount());
                payment.setCreatedAt(LocalDateTime.now());
                payment.setUpdatedAt(LocalDateTime.now());

                paymentRepository.save(payment);

                // Log payment creation - USING PAYMENT VARIABLE
                log.info("Payment {} created for order: {} with amount: {}",
                                payment.getPaymentNumber(), order.getId(), order.getTotalAmount());
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
         * Determine the article type by checking vinyl, cassette, and cd tables
         * CORRECTED: Now queries the actual database tables using correct methods
         */
        private String getArticleType(AnalogArticle article) {
                // Check if article exists in vinyl table - FIXED using correct method
                if (vinylRepository.existsByAnalogArticleId(article.getId())) {
                        return "vinyl";
                }

                // Check if article exists in cassette table - FIXED using correct method
                if (cassetteRepository.existsByAnalogArticleId(article.getId())) {
                        return "cassette";
                }

                // Check if article exists in cd table - FIXED using correct method
                if (cdRepository.existsByAnalogArticleId(article.getId())) {
                        return "cd";
                }

                // This should not happen if data integrity is maintained
                log.warn("Article with ID {} does not exist in any specific type table (vinyl/cassette/cd)",
                                article.getId());
                return "unknown";
        }

        /**
         * Create invoice for order
         */
        private Invoice createInvoice(Order order) {
                String invoiceNumber = generateInvoiceNumber();

                Invoice invoice = new Invoice();
                invoice.setInvoiceNumber(invoiceNumber);
                invoice.setOrder(order);
                invoice.setCurrency(order.getCurrency());
                invoice.setIssueDate(LocalDate.now());
                invoice.setDueDate(LocalDate.now().plusDays(30));
                invoice.setSubtotal(order.getSubtotal());
                invoice.setTaxAmount(order.getTaxAmount());
                invoice.setTotalAmount(order.getTotalAmount());
                invoice.setNotes("Factura generada automáticamente para orden " + order.getOrderNumber());
                invoice.setCreatedAt(LocalDateTime.now());

                return invoiceRepository.save(invoice);
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
         * Map Order entity to OrderSummaryDto - FIXED return type
         */
        private OrderSummaryDto mapToOrderSummary(Order order) {
                return new OrderSummaryDto(
                                order.getId(),
                                order.getOrderNumber(),
                                new StatusDto(order.getOrderStatus().getId(), order.getOrderStatus().getName()),
                                order.getTotalAmount(),
                                new CurrencyDto(order.getCurrency().getCode(), order.getCurrency().getSymbol()),
                                order.getTotalItems(),
                                order.getCreatedAt(),
                                order.getShippedAt(),
                                order.getDeliveredAt());
        }

        /**
         * Map Order entity to OrderDetailResponseDto - FIXED
         */
        private OrderDetailResponseDto mapToOrderDetailResponse(Order order, List<OrderItem> orderItems,
                        List<Payment> payments) {
                // Map items using correct DTO - FIXED
                List<OrderItemDto> itemDtos = orderItems.stream()
                                .map(item -> new OrderItemDto(
                                                item.getId(),
                                                new ArticleBasicDto(
                                                                item.getAnalogArticle().getId(),
                                                                item.getAnalogArticle().getTitle(),
                                                                item.getAnalogArticle().getArtist().getName(),
                                                                getArticleType(item.getAnalogArticle()),
                                                                item.getAnalogArticle().getImageUrl()),
                                                item.getQuantity(),
                                                item.getUnitPrice(),
                                                item.getDiscountAmount(),
                                                item.getTotalPrice(),
                                                item.getCdPromotion() != null ? item.getCdPromotion().getName() : null))
                                .collect(Collectors.toList());

                // Map payments using correct DTO - FIXED
                List<PaymentDto> paymentDtos = payments.stream()
                                .map(payment -> new PaymentDto(
                                                payment.getId(),
                                                payment.getPaymentNumber(),
                                                payment.getPaymentMethod().getName(),
                                                new StatusDto(payment.getPaymentStatus().getId(),
                                                                payment.getPaymentStatus().getName()),
                                                new CurrencyDto(payment.getCurrency().getCode(),
                                                                payment.getCurrency().getSymbol()),
                                                payment.getAmount(),
                                                payment.getTransactionReference(),
                                                payment.getGatewayTransactionId(),
                                                payment.getRefundedAmount(),
                                                payment.getProcessedAt(),
                                                payment.getCreatedAt()))
                                .collect(Collectors.toList());

                // Map addresses
                UserAddressDto shippingAddress = mapToUserAddressDto(order.getShippingAddress());
                UserAddressDto billingAddress = mapToUserAddressDto(order.getBillingAddress());

                return new OrderDetailResponseDto(
                                order.getId(),
                                order.getOrderNumber(),
                                new StatusDto(order.getOrderStatus().getId(), order.getOrderStatus().getName()),
                                new CurrencyDto(order.getCurrency().getCode(), order.getCurrency().getSymbol()),
                                order.getSubtotal(),
                                order.getTaxAmount(),
                                order.getDiscountAmount(),
                                order.getShippingCost(),
                                order.getTotalAmount(),
                                order.getTotalItems(),
                                shippingAddress,
                                billingAddress,
                                order.getNotes(),
                                itemDtos,
                                paymentDtos,
                                order.getCreatedAt(),
                                order.getShippedAt(),
                                order.getDeliveredAt(),
                                order.getUpdatedAt());
        }

        /**
         * Map Invoice and Order to OrderInvoiceResponseDto - FIXED
         */
        private OrderInvoiceResponseDto mapToOrderInvoiceResponse(Invoice invoice, Order order) {
                return new OrderInvoiceResponseDto(
                                invoice.getId(),
                                invoice.getInvoiceNumber(),
                                order.getId(),
                                order.getOrderNumber(),
                                new CurrencyDto(invoice.getCurrency().getCode(), invoice.getCurrency().getSymbol()),
                                invoice.getIssueDate(),
                                invoice.getDueDate(),
                                invoice.getTaxId(),
                                invoice.getSubtotal(),
                                invoice.getTaxAmount(),
                                invoice.getTotalAmount(),
                                invoice.getNotes(),
                                invoice.getPdfUrl(),
                                invoice.getCreatedAt());
        }

        /**
         * Map UserAddress entity to UserAddressDto
         */
        private UserAddressDto mapToUserAddressDto(UserAddress address) {
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
}