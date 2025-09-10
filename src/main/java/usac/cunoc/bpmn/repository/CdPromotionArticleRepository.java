package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.CdPromotionArticle;
import usac.cunoc.bpmn.entity.CdPromotionArticleId;
import java.util.List;

/**
 * CD promotion article junction repository interface
 */
@Repository
public interface CdPromotionArticleRepository extends JpaRepository<CdPromotionArticle, CdPromotionArticleId> {

    /**
     * Find all articles for a promotion
     */
    @Query("SELECT cpa FROM CdPromotionArticle cpa WHERE cpa.cdPromotion.id = :promotionId")
    List<CdPromotionArticle> findByPromotionId(@Param("promotionId") Integer promotionId);

    /**
     * Find all promotions for an article
     */
    @Query("SELECT cpa FROM CdPromotionArticle cpa WHERE cpa.analogArticle.id = :articleId")
    List<CdPromotionArticle> findByArticleId(@Param("articleId") Integer articleId);

    /**
     * Delete all articles for a promotion
     */
    @Modifying
    @Query("DELETE FROM CdPromotionArticle cpa WHERE cpa.cdPromotion.id = :promotionId")
    void deleteByPromotionId(@Param("promotionId") Integer promotionId);

    /**
     * Check if article is in promotion
     */
    @Query("SELECT COUNT(cpa) > 0 FROM CdPromotionArticle cpa " +
            "WHERE cpa.cdPromotion.id = :promotionId AND cpa.analogArticle.id = :articleId")
    boolean existsByPromotionIdAndArticleId(@Param("promotionId") Integer promotionId,
            @Param("articleId") Integer articleId);

    /**
     * Count articles in promotion
     */
    @Query("SELECT COUNT(cpa) FROM CdPromotionArticle cpa WHERE cpa.cdPromotion.id = :promotionId")
    long countByPromotionId(@Param("promotionId") Integer promotionId);
}