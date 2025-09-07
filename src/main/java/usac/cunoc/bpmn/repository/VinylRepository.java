package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.Vinyl;
import java.util.Optional;

/**
 * Repository interface for Vinyl entity operations
 */
@Repository
public interface VinylRepository extends JpaRepository<Vinyl, Integer> {

    /**
     * Find vinyl by analog article ID
     */
    Optional<Vinyl> findByAnalogArticleId(Integer analogArticleId);

    /**
     * Find vinyl with all related data
     */
    @Query("SELECT v FROM Vinyl v " +
            "LEFT JOIN FETCH v.analogArticle " +
            "LEFT JOIN FETCH v.vinylCategory " +
            "LEFT JOIN FETCH v.vinylSpecialEdition " +
            "WHERE v.analogArticle.id = :articleId")
    Optional<Vinyl> findByAnalogArticleIdWithDetails(@Param("articleId") Integer articleId);

    /**
     * Check if vinyl exists for article
     */
    boolean existsByAnalogArticleId(Integer analogArticleId);
}