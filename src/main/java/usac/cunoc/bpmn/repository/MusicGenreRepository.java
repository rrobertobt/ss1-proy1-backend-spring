package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.MusicGenre;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for MusicGenre entity operations
 */
@Repository
public interface MusicGenreRepository extends JpaRepository<MusicGenre, Integer> {

    /**
     * Find genre by name (case insensitive)
     */
    Optional<MusicGenre> findByNameIgnoreCase(String name);

    /**
     * Check if genre name exists (case insensitive)
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Check if genre name exists excluding specific ID (for updates)
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Integer id);

    /**
     * Get all genres that have available articles
     */
    @Query("SELECT DISTINCT mg FROM MusicGenre mg " +
            "JOIN AnalogArticle aa ON mg.id = aa.musicGenre.id " +
            "WHERE aa.isAvailable = true " +
            "ORDER BY mg.name ASC")
    List<MusicGenre> findGenresWithAvailableArticles();

    /**
     * Find all genres ordered by name
     */
    List<MusicGenre> findAllByOrderByNameAsc();

    /**
     * Search genres by name containing
     */
    List<MusicGenre> findByNameContainingIgnoreCaseOrderByNameAsc(String name);

    /**
     * Count articles by genre
     */
    @Query("SELECT COUNT(aa) FROM AnalogArticle aa WHERE aa.musicGenre.id = :genreId")
    long countArticlesByGenre(Integer genreId);

    /**
     * Find genres with article count
     */
    @Query("SELECT mg.id, mg.name, mg.description, COUNT(aa.id) as articleCount " +
            "FROM MusicGenre mg LEFT JOIN AnalogArticle aa ON mg.id = aa.musicGenre.id " +
            "GROUP BY mg.id, mg.name, mg.description " +
            "ORDER BY mg.name ASC")
    List<Object[]> findGenresWithArticleCount();

    /**
     * Check if any articles exist for this genre
     */
    @Query("SELECT COUNT(aa) > 0 FROM AnalogArticle aa WHERE aa.musicGenre.id = :genreId")
    boolean hasArticles(Integer genreId);
}