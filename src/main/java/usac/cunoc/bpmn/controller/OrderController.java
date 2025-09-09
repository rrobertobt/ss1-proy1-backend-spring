package usac.cunoc.bpmn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import usac.cunoc.bpmn.dto.common.ApiResponseDto;
import usac.cunoc.bpmn.dto.order.*;
import usac.cunoc.bpmn.entity.User;
import usac.cunoc.bpmn.repository.UserRepository;
import usac.cunoc.bpmn.service.OrderService;

/**
 * Order controller for order operations - matches PDF specification exactly
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management operations")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Create new order", description = "Create a new order from user's shopping cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or cart empty"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "409", description = "Insufficient stock")
    })
    public ResponseEntity<ApiResponseDto<CreateOrderResponseDto>> createOrder(
            @Valid @RequestBody CreateOrderRequestDto request,
            Authentication authentication) {

        Integer userId = getCurrentUserId(authentication);
        CreateOrderResponseDto response = orderService.createOrder(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("Orden creada exitosamente", response));
    }

    @GetMapping
    @Operation(summary = "Get user orders", description = "Get paginated list of user orders with optional status filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponseDto<OrderListResponseDto>> getUserOrders(
            @RequestParam(defaultValue = "1") @Parameter(description = "Page number") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Items per page") Integer limit,
            @RequestParam(required = false) @Parameter(description = "Filter by order status") String status,
            Authentication authentication) {

        Integer userId = getCurrentUserId(authentication);
        OrderListResponseDto response = orderService.getUserOrders(page, limit, status, userId);

        return ResponseEntity.ok(ApiResponseDto.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order details", description = "Get detailed information about a specific order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order details retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Order not found or access denied")
    })
    public ResponseEntity<ApiResponseDto<OrderDetailResponseDto>> getOrderById(
            @PathVariable @Parameter(description = "Order ID") Integer id,
            Authentication authentication) {

        Integer userId = getCurrentUserId(authentication);
        OrderDetailResponseDto response = orderService.getOrderById(id, userId);

        return ResponseEntity.ok(ApiResponseDto.success(response));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel order", description = "Cancel an existing order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "400", description = "Order cannot be cancelled"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Order not found or access denied")
    })
    public ResponseEntity<ApiResponseDto<CancelOrderResponseDto>> cancelOrder(
            @PathVariable @Parameter(description = "Order ID") Integer id,
            @Valid @RequestBody CancelOrderRequestDto request,
            Authentication authentication) {

        Integer userId = getCurrentUserId(authentication);
        CancelOrderResponseDto response = orderService.cancelOrder(id, request, userId);

        return ResponseEntity.ok(
                ApiResponseDto.success("Orden cancelada exitosamente", response));
    }

    @GetMapping("/{id}/invoice")
    @Operation(summary = "Get order invoice", description = "Get invoice information for an order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invoice retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Order not found or access denied")
    })
    public ResponseEntity<ApiResponseDto<OrderInvoiceResponseDto>> getOrderInvoice(
            @PathVariable @Parameter(description = "Order ID") Integer id,
            Authentication authentication) {

        Integer userId = getCurrentUserId(authentication);
        OrderInvoiceResponseDto response = orderService.getOrderInvoice(id, userId);

        return ResponseEntity.ok(ApiResponseDto.success(response));
    }

    /**
     * Get current user ID from authentication
     */
    private Integer getCurrentUserId(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return user.getId();
    }
}