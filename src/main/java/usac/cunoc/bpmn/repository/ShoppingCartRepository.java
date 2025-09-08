package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.ShoppingCart;
import java.util.Optional;

/**
 * Shopping cart repository interface
 */
@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Integer> {

    /**
     * Find shopping cart by user ID
     */
    @Query("SELECT sc FROM ShoppingCart sc WHERE sc.user.id = :userId")
    Optional<ShoppingCart> findByUserId(@Param("userId") Integer userId);

    /**
     * Check if user has a shopping cart
     */
    @Query("SELECT COUNT(sc) > 0 FROM ShoppingCart sc WHERE sc.user.id = :userId")
    boolean existsByUserId(@Param("userId") Integer userId);

    /**
     * Find shopping cart with items by user ID
     */
    @Query("SELECT sc FROM ShoppingCart sc " +
            "LEFT JOIN FETCH sc.items sci " +
            "LEFT JOIN FETCH sci.analogArticle aa " +
            "LEFT JOIN FETCH aa.artist " +
            "LEFT JOIN FETCH aa.currency " +
            "WHERE sc.user.id = :userId")
    Optional<ShoppingCart> findByUserIdWithItems(@Param("userId") Integer userId);
}