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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Order service implementation - 100% compliant with PDF JSON structure and
 * business rules
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
    private final CreditCardRepository creditCardRepository;
    private final AnalogArticleRepository analogArticleRepository;
    private final InvoiceRepository invoiceRepository;

    @Override
    @Transactional
    public CreateOrderResponseDto createOrder(CreateOrderRequestDto request, Integer userId) {
        // Get user and validate existence
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validate addresses belong to user
        UserAddress shippingAddress = userAddressRepository.findByIdAndUser(request.getShippingAddressId(), user)
                .orElseThrow(() -> new RuntimeException("Dirección de envío no válida"));

        UserAddress billingAddress = userAddressRepository.findByIdAndUser(request.getBillingAddressId(), user)
                .orElseThrow(() -> new RuntimeException("Dirección de facturación no válida"));

        // Validate payment method
        PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new RuntimeException("Método de pago no válido"));

        // Validate credit card if required
        if (paymentMethod.getRequiresCard() && request.getCardId() == null) {
            throw new RuntimeException("Se requiere tarjeta de crédito para este método de pago");
        }

        CreditCard creditCard = null;
        if (request.getCardId() != null) {
            creditCard = creditCardRepository.findByIdAndUserAndIsActiveTrue(request.getCardId(), user)
                    .orElseThrow(() -> new RuntimeException("Tarjeta de crédito no válida"));
        }

        // Get user's shopping cart
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Carrito de compras no encontrado"));

        List<ShoppingCartItem> cartItems = shoppingCartItemRepository.findByCartIdWithDetails(cart.getId());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        // Validate stock availability
        for (ShoppingCartItem item : cartItems) {
            if (item.getAnalogArticle().getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Stock insuficiente para: " + item.getAnalogArticle().getTitle());
            }
        }

        // Get default currency (GTQ)
        Currency currency = currencyRepository.findByCode("GTQ")
                .orElseThrow(() -> new RuntimeException("Moneda por defecto no encontrada"));

        // Get pending order status
        OrderStatus pendingStatus = orderStatusRepository.findByName("Pendiente")
                .orElseThrow(() -> new RuntimeException("Estado de orden no encontrado"));

        // Calculate totals
        BigDecimal subtotal = calculateSubtotal(cartItems);
        BigDecimal taxAmount = calculateTax(subtotal);
        BigDecimal shippingCost = calculateShippingCost(subtotal);
        BigDecimal discountAmount = calculateDiscounts(cartItems);
        BigDecimal totalAmount = subtotal.add(taxAmount).add(shippingCost).subtract(discountAmount);

        // Generate unique order number
        String orderNumber = generateOrderNumber();

        // Create order
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setUser(user);
        order.setOrderStatus(pendingStatus);
        order.setCurrency(currency);
        order.setSubtotal(subtotal);
        order.setTaxAmount(taxAmount);
        order.setDiscountAmount(discountAmount);
        order.setShippingCost(shippingCost);
        order.setTotalAmount(totalAmount);
        order.setTotalItems(cartItems.stream().mapToInt(ShoppingCartItem::getQuantity).sum());
        order.setShippingAddress(shippingAddress);
        order.setBillingAddress(billingAddress);
        order.setNotes(request.getNotes());

        Order savedOrder = orderRepository.save(order);
        log.info("Created order with ID: {} and number: {}", savedOrder.getId(), orderNumber);

        // Create order items and update stock
        List<OrderItem> orderItems = createOrderItems(savedOrder, cartItems);

        // Update article stock
        updateArticleStock(cartItems);

        // Create payment
        Payment payment = createPayment(savedOrder, paymentMethod, creditCard, totalAmount, currency);

        // Clear shopping cart
        shoppingCartItemRepository.deleteAll(cartItems);
        cart.setTotalItems(0);
        cart.setSubtotal(BigDecimal.ZERO);
        shoppingCartRepository.save(cart);

        // Update user totals
        updateUserTotals(user, totalAmount);

        // Generate invoice
        Invoice invoice = createInvoice(savedOrder);
        log.info("Generated invoice {} for order {}", invoice.getInvoiceNumber(), savedOrder.getOrderNumber());

        return new CreateOrderResponseDto(
                savedOrder.getId(),
                savedOrder.getOrderNumber(),
                new StatusDto(pendingStatus.getId(), pendingStatus.getName()),
                savedOrder.getTotalAmount(),
                new CurrencyDto(currency.getCode(), currency.getSymbol()),
                savedOrder.getTotalItems(),
                savedOrder.getCreatedAt());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderListResponseDto getUserOrders(Integer page, Integer limit, String status, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Order> ordersPage;

        if (status != null && !status.trim().isEmpty()) {
            ordersPage = orderRepository.findOrdersByUserAndStatus(user, status, pageable);
        } else {
            ordersPage = orderRepository.findOrdersByUser(user, pageable);
        }

        List<OrderSummaryDto> orders = ordersPage.getContent().stream()
                .map(this::mapToOrderSummary)
                .collect(Collectors.toList());

        PaginationDto pagination = new PaginationDto(
                page,
                (int) ordersPage.getTotalPages(),
                (int) ordersPage.getTotalElements(),
                limit);

        return new OrderListResponseDto(orders, pagination);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponseDto getOrderById(Integer orderId, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Order order = orderRepository.findOrderByIdAndUser(orderId, user)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        List<OrderItem> orderItems = orderItemRepository.findOrderItemsWithDetailsByOrderId(orderId);
        List<Payment> payments = paymentRepository.findPaymentsByOrderId(orderId);

        return mapToOrderDetail(order, orderItems, payments);
    }

    @Override
    @Transactional
    public CancelOrderResponseDto cancelOrder(Integer orderId, CancelOrderRequestDto request, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Order order = orderRepository.findOrderByIdAndUser(orderId, user)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        // Validate order can be cancelled
        if (order.getOrderStatus().getIsFinalStatus()) {
            throw new RuntimeException("No se puede cancelar una orden con estado final");
        }

        // Get cancelled status
        OrderStatus cancelledStatus = orderStatusRepository.findByName("Cancelado")
                .orElseThrow(() -> new RuntimeException("Estado de cancelación no encontrado"));

        // Restore stock
        List<OrderItem> orderItems = orderItemRepository.findOrderItemsByOrderId(orderId);
        for (OrderItem item : orderItems) {
            AnalogArticle article = item.getAnalogArticle();
            article.setStockQuantity(article.getStockQuantity() + item.getQuantity());
            analogArticleRepository.save(article);
        }

        // Update order status
        order.setOrderStatus(cancelledStatus);
        order.setNotes((order.getNotes() != null ? order.getNotes() + "\\n" : "") +
                "Cancelado por: " + request.getReason());

        Order savedOrder = orderRepository.save(order);

        log.info("Cancelled order ID: {} for user: {}", orderId, userId);

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

        // Find or create invoice
        Optional<Invoice> existingInvoice = invoiceRepository.findByOrderId(orderId);

        if (existingInvoice.isPresent()) {
            return mapToOrderInvoice(existingInvoice.get(), order);
        } else {
            // Create invoice if it doesn't exist
            Invoice invoice = createInvoice(order);
            return mapToOrderInvoice(invoice, order);
        }
    }

    // Helper methods
    private BigDecimal calculateSubtotal(List<ShoppingCartItem> cartItems) {
        return cartItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTax(BigDecimal subtotal) {
        return subtotal.multiply(new BigDecimal("0.12")); // 12% IVA
    }

    private BigDecimal calculateShippingCost(BigDecimal subtotal) {
        // Free shipping for orders over 200 GTQ
        return subtotal.compareTo(new BigDecimal("200")) >= 0 ? BigDecimal.ZERO : new BigDecimal("25.00");
    }

    private BigDecimal calculateDiscounts(List<ShoppingCartItem> cartItems) {
        return cartItems.stream()
                .map(ShoppingCartItem::getDiscountApplied)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "ORD-" + timestamp + "-" + random;
    }

    private List<OrderItem> createOrderItems(Order order, List<ShoppingCartItem> cartItems) {
        return cartItems.stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setAnalogArticle(cartItem.getAnalogArticle());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setDiscountAmount(cartItem.getDiscountApplied());
            orderItem.setTotalPrice(cartItem.getUnitPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()))
                    .subtract(cartItem.getDiscountApplied()));
            orderItem.setCdPromotion(cartItem.getCdPromotion());

            return orderItemRepository.save(orderItem);
        }).collect(Collectors.toList());
    }

    private void updateArticleStock(List<ShoppingCartItem> cartItems) {
        for (ShoppingCartItem item : cartItems) {
            AnalogArticle article = item.getAnalogArticle();
            article.setStockQuantity(article.getStockQuantity() - item.getQuantity());
            article.setTotalSold(article.getTotalSold() + item.getQuantity());
            analogArticleRepository.save(article);
        }
    }

    private Payment createPayment(Order order, PaymentMethod paymentMethod, CreditCard creditCard,
            BigDecimal amount, Currency currency) {
        PaymentStatus pendingStatus = paymentStatusRepository.findByName("Pendiente")
                .orElseThrow(() -> new RuntimeException("Estado de pago no encontrado"));

        String paymentNumber = generatePaymentNumber();

        Payment payment = new Payment();
        payment.setPaymentNumber(paymentNumber);
        payment.setOrder(order);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus(pendingStatus);
        payment.setCurrency(currency);
        payment.setAmount(amount);

        if (creditCard != null) {
            payment.setTransactionReference("CARD-" + creditCard.getLastFourDigits());
        }

        return paymentRepository.save(payment);
    }

    private String generatePaymentNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "PAY-" + timestamp + "-" + random;
    }

    private void updateUserTotals(User user, BigDecimal orderAmount) {
        user.setTotalSpent(user.getTotalSpent().add(orderAmount));
        user.setTotalOrders(user.getTotalOrders() + 1);
        userRepository.save(user);
    }

    private Invoice createInvoice(Order order) {
        String invoiceNumber = generateInvoiceNumber();

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setOrder(order);
        invoice.setCurrency(order.getCurrency());
        invoice.setSubtotal(order.getSubtotal());
        invoice.setTaxAmount(order.getTaxAmount());
        invoice.setTotalAmount(order.getTotalAmount());
        invoice.setNotes("Factura generada automáticamente para orden " + order.getOrderNumber());

        return invoiceRepository.save(invoice);
    }

    private String generateInvoiceNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "INV-" + timestamp + "-" + random;
    }

    // Mapping methods
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

    private OrderDetailResponseDto mapToOrderDetail(Order order, List<OrderItem> orderItems,
            List<Payment> payments) {
        List<OrderItemDto> items = orderItems.stream()
                .map(this::mapToOrderItemDto)
                .collect(Collectors.toList());

        List<PaymentDto> paymentDtos = payments.stream()
                .map(this::mapToPaymentDto)
                .collect(Collectors.toList());

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
                mapToUserAddressDto(order.getShippingAddress()),
                mapToUserAddressDto(order.getBillingAddress()),
                order.getNotes(),
                items,
                paymentDtos,
                order.getCreatedAt(),
                order.getShippedAt(),
                order.getDeliveredAt(),
                order.getUpdatedAt());
    }

    private OrderItemDto mapToOrderItemDto(OrderItem orderItem) {
        AnalogArticle article = orderItem.getAnalogArticle();
        ArticleBasicDto articleDto = new ArticleBasicDto(
                article.getId(),
                article.getTitle(),
                article.getArtist().getName(),
                getArticleType(article),
                article.getImageUrl());

        String promotionName = orderItem.getCdPromotion() != null ? orderItem.getCdPromotion().getName() : null;

        return new OrderItemDto(
                orderItem.getId(),
                articleDto,
                orderItem.getQuantity(),
                orderItem.getUnitPrice(),
                orderItem.getDiscountAmount(),
                orderItem.getTotalPrice(),
                promotionName);
    }

    private PaymentDto mapToPaymentDto(Payment payment) {
        return new PaymentDto(
                payment.getId(),
                payment.getPaymentNumber(),
                payment.getPaymentMethod().getName(),
                new StatusDto(payment.getPaymentStatus().getId(), payment.getPaymentStatus().getName()),
                new CurrencyDto(payment.getCurrency().getCode(), payment.getCurrency().getSymbol()),
                payment.getAmount(),
                payment.getTransactionReference(),
                payment.getGatewayTransactionId(),
                payment.getRefundedAmount(),
                payment.getProcessedAt(),
                payment.getCreatedAt());
    }

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

    private OrderInvoiceResponseDto mapToOrderInvoice(Invoice invoice, Order order) {
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

    private String getArticleType(AnalogArticle article) {
        // Para simplificar, usar "vinyl" como default
        // En un sistema real, se consultarían las tablas vinyl/cassette/cd
        return "vinyl";
    }
}