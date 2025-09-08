package usac.cunoc.bpmn.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import usac.cunoc.bpmn.dto.cart.*;
import usac.cunoc.bpmn.dto.common.ApiResponseDto;
import usac.cunoc.bpmn.service.CartService;
import usac.cunoc.bpmn.repository.UserRepository;

/**
 * Cart controller - handles shopping cart operations
 */
@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cart", description = "Shopping cart management operations")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    @Operation(summary = "Get user's shopping cart", description = "Retrieve current user's shopping cart with all items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDto<CartResponseDto>> getCart(
            @AuthenticationPrincipal UserDetails userDetails) {

        Integer userId = getUserIdFromUserDetails(userDetails);
        CartResponseDto cart = cartService.getCart(userId);

        return ResponseEntity.ok(ApiResponseDto.success(cart));
    }

    @Operation(summary = "Add item to cart", description = "Add an article to the shopping cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item added successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or insufficient stock"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Article not found")
    })
    @PostMapping("/items")
    public ResponseEntity<ApiResponseDto<AddCartItemResponseDto>> addItemToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddCartItemRequestDto request) {

        Integer userId = getUserIdFromUserDetails(userDetails);
        AddCartItemResponseDto response = cartService.addItemToCart(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("Artículo agregado al carrito exitosamente", response));
    }

    @Operation(summary = "Update cart item quantity", description = "Update the quantity of an item in the cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quantity updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or insufficient stock"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Cart item not found")
    })
    @PutMapping("/items/{id}")
    public ResponseEntity<ApiResponseDto<UpdateCartItemResponseDto>> updateCartItemQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Cart item ID") @PathVariable Integer id,
            @Valid @RequestBody UpdateCartItemRequestDto request) {

        Integer userId = getUserIdFromUserDetails(userDetails);
        UpdateCartItemResponseDto response = cartService.updateCartItemQuantity(userId, id, request);

        return ResponseEntity.ok(ApiResponseDto.success("Cantidad actualizada exitosamente", response));
    }

    @Operation(summary = "Remove item from cart", description = "Remove an item from the shopping cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item removed successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Cart item not found")
    })
    @DeleteMapping("/items/{id}")
    public ResponseEntity<ApiResponseDto<RemoveCartItemResponseDto>> removeItemFromCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Cart item ID") @PathVariable Integer id) {

        Integer userId = getUserIdFromUserDetails(userDetails);
        RemoveCartItemResponseDto response = cartService.removeItemFromCart(userId, id);

        return ResponseEntity.ok(ApiResponseDto.success("Artículo removido del carrito exitosamente", response));
    }

    @Operation(summary = "Clear cart", description = "Remove all items from the shopping cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart cleared successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping
    public ResponseEntity<ApiResponseDto<Void>> clearCart(
            @AuthenticationPrincipal UserDetails userDetails) {

        Integer userId = getUserIdFromUserDetails(userDetails);
        cartService.clearCart(userId);

        return ResponseEntity.ok(ApiResponseDto.success("Carrito vaciado exitosamente"));
    }

    @Operation(summary = "Apply CD promotion", description = "Apply a CD promotion to selected cart items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promotion applied successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid promotion or items"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Promotion not found")
    })
    @PostMapping("/apply-cd-promotion")
    public ResponseEntity<ApiResponseDto<ApplyPromotionResponseDto>> applyCdPromotion(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ApplyPromotionRequestDto request) {

        Integer userId = getUserIdFromUserDetails(userDetails);
        ApplyPromotionResponseDto response = cartService.applyCdPromotion(userId, request);

        return ResponseEntity.ok(ApiResponseDto.success("Promoción aplicada exitosamente", response));
    }

    /**
     * Extract user ID from UserDetails
     */
    private Integer getUserIdFromUserDetails(UserDetails userDetails) {
        return userRepository.findByUsernameOrEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();
    }
}