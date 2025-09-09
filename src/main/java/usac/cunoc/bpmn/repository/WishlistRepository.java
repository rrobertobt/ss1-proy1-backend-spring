package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.Wishlist;
import java.util.Optional;

/**
 * Repository interface for Wishlist entity operations
 * Handles database operations for user wishlists
 */
@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {

    /**
     * Find wishlist by user ID
     */
    @Query("SELECT w FROM Wishlist w WHERE w.user.id = :userId")
    Optional<Wishlist> findByUserId(@Param("userId") Integer userId);

    /**
     * Check if wishlist exists for user
     */
    @Query("SELECT COUNT(w) > 0 FROM Wishlist w WHERE w.user.id = :userId")
    boolean existsByUserId(@Param("userId") Integer userId);
}