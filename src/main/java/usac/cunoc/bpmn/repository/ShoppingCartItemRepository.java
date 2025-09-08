package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.ShoppingCartItem;
import java.util.List;
import java.util.Optional;

/**
 * Shopping cart item repository interface
 */
@Repository
public interface ShoppingCartItemRepository extends JpaRepository<ShoppingCartItem, Integer> {

    /**
     * Find cart item by shopping cart ID and analog article ID
     */
    @Query("SELECT sci FROM ShoppingCartItem sci " +
            "WHERE sci.shoppingCart.id = :cartId AND sci.analogArticle.id = :articleId")
    Optional<ShoppingCartItem> findByCartIdAndArticleId(@Param("cartId") Integer cartId,
            @Param("articleId") Integer articleId);

    /**
     * Find all items by shopping cart ID
     */
    @Query("SELECT sci FROM ShoppingCartItem sci " +
            "LEFT JOIN FETCH sci.analogArticle aa " +
            "LEFT JOIN FETCH aa.artist " +
            "LEFT JOIN FETCH aa.currency " +
            "WHERE sci.shoppingCart.id = :cartId " +
            "ORDER BY sci.createdAt DESC")
    List<ShoppingCartItem> findByCartIdWithDetails(@Param("cartId") Integer cartId);

    /**
     * Delete all items by shopping cart ID
     */
    @Modifying
    @Query("DELETE FROM ShoppingCartItem sci WHERE sci.shoppingCart.id = :cartId")
    void deleteByShoppingCartId(@Param("cartId") Integer cartId);

    /**
     * Count items in cart
     */
    @Query("SELECT COUNT(sci) FROM ShoppingCartItem sci WHERE sci.shoppingCart.id = :cartId")
    Long countByShoppingCartId(@Param("cartId") Integer cartId);

    /**
     * Find items by promotion ID for CD promotions
     */
    @Query("SELECT sci FROM ShoppingCartItem sci " +
            "WHERE sci.shoppingCart.id = :cartId AND sci.cdPromotion.id = :promotionId")
    List<ShoppingCartItem> findByCartIdAndPromotionId(@Param("cartId") Integer cartId,
            @Param("promotionId") Integer promotionId);

    /**
     * Check if article exists in cart
     */
    @Query("SELECT COUNT(sci) > 0 FROM ShoppingCartItem sci " +
            "WHERE sci.shoppingCart.id = :cartId AND sci.analogArticle.id = :articleId")
    boolean existsByCartIdAndArticleId(@Param("cartId") Integer cartId, @Param("articleId") Integer articleId);
}