package usac.cunoc.bpmn.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.StockMovement;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for StockMovement entity operations
 */
@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Integer> {

    /**
     * Find stock movements by article with pagination
     */
    @Query("SELECT sm FROM StockMovement sm " +
            "LEFT JOIN FETCH sm.analogArticle " +
            "LEFT JOIN FETCH sm.movementType " +
            "LEFT JOIN FETCH sm.movementReferenceType " +
            "LEFT JOIN FETCH sm.createdByUser " +
            "WHERE sm.analogArticle.id = :articleId " +
            "ORDER BY sm.createdAt DESC")
    Page<StockMovement> findMovementsByArticleId(@Param("articleId") Integer articleId, Pageable pageable);

    /**
     * Find all stock movements with filters
     */
    @Query("SELECT sm FROM StockMovement sm " +
            "LEFT JOIN FETCH sm.analogArticle aa " +
            "LEFT JOIN FETCH aa.artist " +
            "LEFT JOIN FETCH sm.movementType mt " +
            "LEFT JOIN FETCH sm.movementReferenceType " +
            "LEFT JOIN FETCH sm.createdByUser " +
            "WHERE (:articleId IS NULL OR sm.analogArticle.id = :articleId) " +
            "AND (:movementType IS NULL OR mt.name = :movementType) " +
            "AND (:dateFrom IS NULL OR DATE(sm.createdAt) >= :dateFrom) " +
            "AND (:dateTo IS NULL OR DATE(sm.createdAt) <= :dateTo) " +
            "ORDER BY sm.createdAt DESC")
    Page<StockMovement> findMovementsWithFilters(
            @Param("articleId") Integer articleId,
            @Param("movementType") String movementType,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            Pageable pageable);

    /**
     * Find recent movements for an article
     */
    @Query("SELECT sm FROM StockMovement sm " +
            "LEFT JOIN FETCH sm.movementType " +
            "WHERE sm.analogArticle.id = :articleId " +
            "ORDER BY sm.createdAt DESC")
    List<StockMovement> findRecentMovementsByArticleId(@Param("articleId") Integer articleId, Pageable pageable);

    /**
     * Find movements by user (for audit trail)
     */
    @Query("SELECT sm FROM StockMovement sm " +
            "LEFT JOIN FETCH sm.analogArticle " +
            "LEFT JOIN FETCH sm.movementType " +
            "WHERE sm.createdByUser.id = :userId " +
            "ORDER BY sm.createdAt DESC")
    Page<StockMovement> findMovementsByUserId(@Param("userId") Integer userId, Pageable pageable);

    /**
     * Get stock movements for a date range (for reporting)
     */
    @Query("SELECT sm FROM StockMovement sm " +
            "LEFT JOIN FETCH sm.analogArticle " +
            "LEFT JOIN FETCH sm.movementType " +
            "WHERE DATE(sm.createdAt) BETWEEN :startDate AND :endDate " +
            "ORDER BY sm.createdAt DESC")
    List<StockMovement> findMovementsByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}