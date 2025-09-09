package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.WishlistItem;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for WishlistItem entity operations
 * Handles database operations for wishlist items with detailed article information
 */
@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Integer> {

    /**
     * Find all wishlist items by wishlist ID with article details
     */
    @Query("SELECT wi FROM WishlistItem wi " +
           "JOIN FETCH wi.analogArticle aa " +
           "JOIN FETCH aa.artist a " +
           "JOIN FETCH aa.currency c " +
           "WHERE wi.wishlist.id = :wishlistId " +
           "ORDER BY wi.createdAt DESC")
    List<WishlistItem> findByWishlistIdWithDetails(@Param("wishlistId") Integer wishlistId);

    /**
     * Find specific wishlist item by wishlist and article
     */
    @Query("SELECT wi FROM WishlistItem wi " +
           "WHERE wi.wishlist.id = :wishlistId " +
           "AND wi.analogArticle.id = :articleId")
    Optional<WishlistItem> findByWishlistIdAndArticleId(@Param("wishlistId") Integer wishlistId, 
                                                         @Param("articleId") Integer articleId);

    /**
     * Check if item exists in user's wishlist
     */
    @Query("SELECT COUNT(wi) > 0 FROM WishlistItem wi " +
           "WHERE wi.wishlist.user.id = :userId " +
           "AND wi.analogArticle.id = :articleId")
    boolean existsByUserIdAndArticleId(@Param("userId") Integer userId, 
                                       @Param("articleId") Integer articleId);

    /**
     * Find wishlist item by ID and user ID for security validation
     */
    @Query("SELECT wi FROM WishlistItem wi " +
           "WHERE wi.id = :itemId " +
           "AND wi.wishlist.user.id = :userId")
    Optional<WishlistItem> findByIdAndUserId(@Param("itemId") Integer itemId, 
                                             @Param("userId") Integer userId);
}