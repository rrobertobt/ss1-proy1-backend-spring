package usac.cunoc.bpmn.service;

import usac.cunoc.bpmn.dto.wishlist.*;

/**
 * Wishlist service interface - defines contract for wishlist operations
 * Matches PDF specification exactly for /api/v1/wishlist endpoints
 */
public interface WishlistService {

    /**
     * Get user's complete wishlist with all items
     * 
     * @param userId User ID to get wishlist for
     * @return WishlistResponseDto with all wishlist items
     */
    WishlistResponseDto getWishlist(Integer userId);

    /**
     * Add article to user's wishlist
     * 
     * @param userId  User ID
     * @param request Request containing article ID to add
     * @return AddWishlistItemResponseDto with added item details
     */
    AddWishlistItemResponseDto addItemToWishlist(Integer userId, AddWishlistItemRequestDto request);

    /**
     * Remove item from user's wishlist
     * 
     * @param itemId Wishlist item ID to remove
     * @param userId User ID for security validation
     * @return RemoveWishlistItemResponseDto with removal confirmation
     */
    RemoveWishlistItemResponseDto removeItemFromWishlist(Integer itemId, Integer userId);

    /**
     * Process preorder payment for wishlist item
     * 
     * @param itemId  Wishlist item ID
     * @param request Payment details
     * @param userId  User ID for security validation
     * @return WishlistPreorderPaymentResponseDto with payment confirmation
     */
    WishlistPreorderPaymentResponseDto processPreorderPayment(Integer itemId, 
                                                               WishlistPreorderPaymentRequestDto request, 
                                                               Integer userId);
}