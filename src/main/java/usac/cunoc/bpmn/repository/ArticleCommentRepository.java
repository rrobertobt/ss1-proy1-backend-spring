package usac.cunoc.bpmn.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.ArticleComment;
import java.util.List;

/**
 * Repository interface for ArticleComment entity operations
 */
@Repository
public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Integer> {

        /**
         * Find top-level comments for an article (visible comments only)
         */
        @Query("SELECT ac FROM ArticleComment ac " +
                        "LEFT JOIN FETCH ac.user " +
                        "LEFT JOIN FETCH ac.commentStatus " +
                        "WHERE ac.analogArticle.id = :articleId " +
                        "AND ac.parentComment IS NULL " +
                        "AND ac.commentStatus.isVisible = true " +
                        "ORDER BY ac.createdAt DESC")
        Page<ArticleComment> findTopLevelCommentsByArticleId(@Param("articleId") Integer articleId, Pageable pageable);

        /**
         * Find replies for a parent comment (visible comments only)
         */
        @Query("SELECT ac FROM ArticleComment ac " +
                        "LEFT JOIN FETCH ac.user " +
                        "LEFT JOIN FETCH ac.commentStatus " +
                        "WHERE ac.parentComment.id = :parentCommentId " +
                        "AND ac.commentStatus.isVisible = true " +
                        "ORDER BY ac.createdAt ASC")
        List<ArticleComment> findRepliesByParentCommentId(@Param("parentCommentId") Integer parentCommentId);

        /**
         * Count total comments for an article (visible comments only)
         */
        @Query("SELECT COUNT(ac) FROM ArticleComment ac " +
                        "WHERE ac.analogArticle.id = :articleId " +
                        "AND ac.commentStatus.isVisible = true")
        Integer countVisibleCommentsByArticleId(@Param("articleId") Integer articleId);

        /**
         * Find all comments by user (for admin purposes)
         */
        @Query("SELECT ac FROM ArticleComment ac " +
                        "LEFT JOIN FETCH ac.analogArticle " +
                        "LEFT JOIN FETCH ac.commentStatus " +
                        "WHERE ac.user.id = :userId " +
                        "ORDER BY ac.createdAt DESC")
        Page<ArticleComment> findCommentsByUserId(@Param("userId") Integer userId, Pageable pageable);

        /**
         * Find deleted comments for violations tracking
         */
        @Query("SELECT ac FROM ArticleComment ac " +
                        "LEFT JOIN FETCH ac.analogArticle " +
                        "LEFT JOIN FETCH ac.deletedByUser " +
                        "WHERE ac.user.id = :userId " +
                        "AND ac.commentStatus.name = 'Eliminado' " +
                        "ORDER BY ac.deletedAt DESC")
        List<ArticleComment> findDeletedCommentsByUserId(@Param("userId") Integer userId);

        /**
         * Find comments by status name with pagination (for admin moderation)
         */
        @Query("SELECT ac FROM ArticleComment ac " +
                        "LEFT JOIN FETCH ac.user " +
                        "LEFT JOIN FETCH ac.analogArticle " +
                        "LEFT JOIN FETCH ac.commentStatus " +
                        "WHERE ac.commentStatus.name = :statusName " +
                        "ORDER BY ac.updatedAt DESC")
        Page<ArticleComment> findByCommentStatusName(@Param("statusName") String statusName, Pageable pageable);
}