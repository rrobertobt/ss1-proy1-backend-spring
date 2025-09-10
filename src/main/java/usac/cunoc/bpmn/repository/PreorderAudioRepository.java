package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.PreorderAudio;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for PreorderAudio entity operations
 */
@Repository
public interface PreorderAudioRepository extends JpaRepository<PreorderAudio, Integer> {

    /**
     * Find all preorder audios for a specific article
     */
    @Query("SELECT pa FROM PreorderAudio pa " +
            "LEFT JOIN FETCH pa.analogArticle " +
            "WHERE pa.analogArticle.id = :articleId " +
            "ORDER BY pa.createdAt ASC")
    List<PreorderAudio> findByAnalogArticleId(@Param("articleId") Integer articleId);

    /**
     * Find preorder audio by ID with article details
     */
    @Query("SELECT pa FROM PreorderAudio pa " +
            "LEFT JOIN FETCH pa.analogArticle aa " +
            "LEFT JOIN FETCH aa.artist " +
            "WHERE pa.id = :audioId")
    Optional<PreorderAudio> findByIdWithArticleDetails(@Param("audioId") Integer audioId);

    /**
     * Check if preorder audio exists for article
     */
    boolean existsByAnalogArticleId(Integer articleId);

    /**
     * Update download count
     */
    @Modifying
    @Query("UPDATE PreorderAudio pa SET pa.downloadCount = pa.downloadCount + 1 WHERE pa.id = :audioId")
    void incrementDownloadCount(@Param("audioId") Integer audioId);

    /**
     * Find downloadable preorder audios for article
     */
    @Query("SELECT pa FROM PreorderAudio pa " +
            "WHERE pa.analogArticle.id = :articleId " +
            "AND pa.isDownloadable = true " +
            "ORDER BY pa.createdAt ASC")
    List<PreorderAudio> findDownloadableByArticleId(@Param("articleId") Integer articleId);
}