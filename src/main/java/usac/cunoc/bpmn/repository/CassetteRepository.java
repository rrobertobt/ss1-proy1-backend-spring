package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.Cassette;
import java.util.Optional;

/**
 * Repository interface for Cassette entity operations
 */
@Repository
public interface CassetteRepository extends JpaRepository<Cassette, Integer> {

    /**
     * Find cassette by analog article ID
     */
    Optional<Cassette> findByAnalogArticleId(Integer analogArticleId);

    /**
     * Find cassette with all related data
     */
    @Query("SELECT c FROM Cassette c " +
            "LEFT JOIN FETCH c.analogArticle " +
            "LEFT JOIN FETCH c.cassetteCategory " +
            "WHERE c.analogArticle.id = :articleId")
    Optional<Cassette> findByAnalogArticleIdWithDetails(@Param("articleId") Integer articleId);

    /**
     * Check if cassette exists for article
     */
    boolean existsByAnalogArticleId(Integer analogArticleId);
}