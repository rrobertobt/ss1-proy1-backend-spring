package usac.cunoc.bpmn.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.ArticleRating;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * Repository interface for ArticleRating entity operations
 */
@Repository
public interface ArticleRatingRepository extends JpaRepository<ArticleRating, Integer> {

    /**
     * Find ratings for an article with pagination
     */
    @Query("SELECT ar FROM ArticleRating ar " +
            "LEFT JOIN FETCH ar.user " +
            "WHERE ar.analogArticle.id = :articleId " +
            "ORDER BY ar.createdAt DESC")
    Page<ArticleRating> findRatingsByArticleId(@Param("articleId") Integer articleId, Pageable pageable);

    /**
     * Find rating by article and user (unique constraint)
     */
    Optional<ArticleRating> findByAnalogArticleIdAndUserId(Integer articleId, Integer userId);

    /**
     * Check if user has already rated an article
     */
    boolean existsByAnalogArticleIdAndUserId(Integer articleId, Integer userId);

    /**
     * Calculate average rating for an article
     */
    @Query("SELECT AVG(ar.rating) FROM ArticleRating ar WHERE ar.analogArticle.id = :articleId")
    BigDecimal findAverageRatingByArticleId(@Param("articleId") Integer articleId);

    /**
     * Count total ratings for an article
     */
    @Query("SELECT COUNT(ar) FROM ArticleRating ar WHERE ar.analogArticle.id = :articleId")
    Integer countRatingsByArticleId(@Param("articleId") Integer articleId);

    /**
     * Get rating distribution for an article (count by rating value)
     */
    @Query("SELECT ar.rating, COUNT(ar.rating) FROM ArticleRating ar " +
            "WHERE ar.analogArticle.id = :articleId " +
            "GROUP BY ar.rating " +
            "ORDER BY ar.rating DESC")
    Object[][] findRatingDistributionByArticleId(@Param("articleId") Integer articleId);

    /**
     * Find ratings with review text sorted by helpful votes
     */
    @Query("SELECT ar FROM ArticleRating ar " +
            "LEFT JOIN FETCH ar.user " +
            "WHERE ar.analogArticle.id = :articleId " +
            "AND ar.reviewText IS NOT NULL " +
            "ORDER BY ar.helpfulVotes DESC, ar.createdAt DESC")
    Page<ArticleRating> findRatingsWithReviewByArticleId(@Param("articleId") Integer articleId, Pageable pageable);

    /**
     * Find verified purchase ratings
     */
    @Query("SELECT ar FROM ArticleRating ar " +
            "LEFT JOIN FETCH ar.user " +
            "WHERE ar.analogArticle.id = :articleId " +
            "AND ar.isVerifiedPurchase = true " +
            "ORDER BY ar.createdAt DESC")
    Page<ArticleRating> findVerifiedRatingsByArticleId(@Param("articleId") Integer articleId, Pageable pageable);
}