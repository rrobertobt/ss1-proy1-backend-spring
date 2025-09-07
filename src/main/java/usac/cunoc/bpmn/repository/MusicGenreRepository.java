package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.MusicGenre;
import java.util.List;

/**
 * Repository interface for MusicGenre entity operations
 */
@Repository
public interface MusicGenreRepository extends JpaRepository<MusicGenre, Integer> {

    /**
     * Find genre by name (case insensitive)
     */
    MusicGenre findByNameIgnoreCase(String name);

    /**
     * Check if genre name exists (case insensitive)
     */
    boolean existsByNameIgnoreCase(String name);

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
}