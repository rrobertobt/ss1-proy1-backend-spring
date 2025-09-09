package usac.cunoc.bpmn.service;

import usac.cunoc.bpmn.dto.order.*;

/**
 * Order service interface for order business logic operations
 */
public interface OrderService {

    /**
     * Create a new order from user's shopping cart
     */
    CreateOrderResponseDto createOrder(CreateOrderRequestDto request, Integer userId);

    /**
     * Get paginated list of user orders with optional status filter
     */
    OrderListResponseDto getUserOrders(Integer page, Integer limit, String status, Integer userId);

    /**
     * Get detailed information about a specific order
     */
    OrderDetailResponseDto getOrderById(Integer orderId, Integer userId);

    /**
     * Cancel an existing order
     */
    CancelOrderResponseDto cancelOrder(Integer orderId, CancelOrderRequestDto request, Integer userId);

    /**
     * Get invoice information for an order
     */
    OrderInvoiceResponseDto getOrderInvoice(Integer orderId, Integer userId);
}