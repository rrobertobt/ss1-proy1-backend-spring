package usac.cunoc.bpmn.service;

import usac.cunoc.bpmn.dto.cart.*;

/**
 * Cart service interface - handles shopping cart operations
 */
public interface CartService {

    /**
     * Get user's shopping cart with all items
     * 
     * @param userId User ID
     * @return Cart with items
     */
    CartResponseDto getCart(Integer userId);

    /**
     * Add item to shopping cart
     * 
     * @param userId  User ID
     * @param request Add item request
     * @return Add item response
     */
    AddCartItemResponseDto addItemToCart(Integer userId, AddCartItemRequestDto request);

    /**
     * Update cart item quantity
     * 
     * @param userId  User ID
     * @param itemId  Cart item ID
     * @param request Update request
     * @return Update response
     */
    UpdateCartItemResponseDto updateCartItemQuantity(Integer userId, Integer itemId,
            UpdateCartItemRequestDto request);

    /**
     * Remove item from cart
     * 
     * @param userId User ID
     * @param itemId Cart item ID
     * @return Removal response with updated cart info
     */
    RemoveCartItemResponseDto removeItemFromCart(Integer userId, Integer itemId);

    /**
     * Clear all items from cart
     * 
     * @param userId User ID
     */
    void clearCart(Integer userId);

    /**
     * Apply CD promotion to cart items
     * 
     * @param userId  User ID
     * @param request Promotion request
     * @return Promotion application response
     */
    ApplyPromotionResponseDto applyCdPromotion(Integer userId, ApplyPromotionRequestDto request);
}