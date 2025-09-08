package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.CdPromotion;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * CD promotion repository interface
 */
@Repository
public interface CdPromotionRepository extends JpaRepository<CdPromotion, Integer> {

    /**
     * Find active promotions
     */
    @Query("SELECT cp FROM CdPromotion cp " +
            "WHERE cp.isActive = true " +
            "AND (cp.endDate IS NULL OR cp.endDate > :currentDate)")
    List<CdPromotion> findActivePromotions(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Find promotions by genre
     */
    @Query("SELECT cp FROM CdPromotion cp " +
            "WHERE cp.isActive = true " +
            "AND cp.musicGenre.id = :genreId " +
            "AND (cp.endDate IS NULL OR cp.endDate > :currentDate)")
    List<CdPromotion> findActivePromotionsByGenre(@Param("genreId") Integer genreId,
            @Param("currentDate") LocalDateTime currentDate);

    /**
     * Find random promotions (not genre-based)
     */
    @Query("SELECT cp FROM CdPromotion cp " +
            "WHERE cp.isActive = true " +
            "AND cp.musicGenre IS NULL " +
            "AND (cp.endDate IS NULL OR cp.endDate > :currentDate)")
    List<CdPromotion> findActiveRandomPromotions(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Find promotion by ID if active
     */
    @Query("SELECT cp FROM CdPromotion cp " +
            "WHERE cp.id = :promotionId " +
            "AND cp.isActive = true " +
            "AND (cp.endDate IS NULL OR cp.endDate > :currentDate)")
    Optional<CdPromotion> findActivePromotionById(@Param("promotionId") Integer promotionId,
            @Param("currentDate") LocalDateTime currentDate);
}