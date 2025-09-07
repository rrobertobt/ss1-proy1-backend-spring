package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.Cd;
import java.util.Optional;

/**
 * Repository interface for Cd entity operations
 */
@Repository
public interface CdRepository extends JpaRepository<Cd, Integer> {

    /**
     * Find CD by analog article ID
     */
    Optional<Cd> findByAnalogArticleId(Integer analogArticleId);

    /**
     * Find CD with all related data
     */
    @Query("SELECT cd FROM Cd cd " +
            "LEFT JOIN FETCH cd.analogArticle " +
            "WHERE cd.analogArticle.id = :articleId")
    Optional<Cd> findByAnalogArticleIdWithDetails(@Param("articleId") Integer articleId);

    /**
     * Check if CD exists for article
     */
    boolean existsByAnalogArticleId(Integer analogArticleId);
}