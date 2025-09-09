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
import usac.cunoc.bpmn.dto.wishlist.*;
import usac.cunoc.bpmn.entity.User;
import usac.cunoc.bpmn.repository.UserRepository;
import usac.cunoc.bpmn.service.WishlistService;

/**
 * Wishlist controller for user wishlist operations - matches PDF specification exactly
 * Handles all wishlist endpoints: GET, POST, DELETE and preorder payment
 */
@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
@Tag(name = "Wishlist", description = "User wishlist management operations")
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get user wishlist", description = "Get current user's complete wishlist with all items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wishlist retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponseDto<WishlistResponseDto>> getWishlist(Authentication authentication) {
        Integer userId = getCurrentUserId(authentication);
        WishlistResponseDto wishlist = wishlistService.getWishlist(userId);
        return ResponseEntity.ok(ApiResponseDto.success(wishlist));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to wishlist", description = "Add an article to user's wishlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item added to wishlist successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or item already in wishlist"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User or article not found")
    })
    public ResponseEntity<ApiResponseDto<AddWishlistItemResponseDto>> addItemToWishlist(
            @Valid @RequestBody AddWishlistItemRequestDto request,
            Authentication authentication) {

        Integer userId = getCurrentUserId(authentication);
        AddWishlistItemResponseDto response = wishlistService.addItemToWishlist(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("Artículo agregado a la lista de deseos exitosamente", response));
    }

    @DeleteMapping("/items/{id}")
    @Operation(summary = "Remove item from wishlist", description = "Remove an item from user's wishlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item removed from wishlist successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Wishlist item not found")
    })
    public ResponseEntity<ApiResponseDto<RemoveWishlistItemResponseDto>> removeItemFromWishlist(
            @PathVariable @Parameter(description = "Wishlist item ID") Integer id,
            Authentication authentication) {

        Integer userId = getCurrentUserId(authentication);
        RemoveWishlistItemResponseDto response = wishlistService.removeItemFromWishlist(id, userId);

        return ResponseEntity.ok(
                ApiResponseDto.success("Artículo removido de la lista de deseos exitosamente", response));
    }

    @PostMapping("/items/{id}/preorder-payment")
    @Operation(summary = "Pay for preorder from wishlist", description = "Process payment for a preorder article in wishlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preorder payment processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payment data or preorder already paid"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Wishlist item or payment method not found")
    })
    public ResponseEntity<ApiResponseDto<WishlistPreorderPaymentResponseDto>> processPreorderPayment(
            @PathVariable @Parameter(description = "Wishlist item ID") Integer id,
            @Valid @RequestBody WishlistPreorderPaymentRequestDto request,
            Authentication authentication) {

        Integer userId = getCurrentUserId(authentication);
        WishlistPreorderPaymentResponseDto response = wishlistService.processPreorderPayment(id, request, userId);

        return ResponseEntity.ok(
                ApiResponseDto.success("Preorden pagada exitosamente", response));
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