package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.Artist;
import java.util.List;

/**
 * Repository interface for Artist entity operations
 */
@Repository
public interface ArtistRepository extends JpaRepository<Artist, Integer> {

    /**
     * Find artist by name (case insensitive)
     */
    Artist findByNameIgnoreCase(String name);

    /**
     * Check if artist name exists (case insensitive)
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Get all artists that have available articles
     */
    @Query("SELECT DISTINCT a FROM Artist a " +
            "JOIN AnalogArticle aa ON a.id = aa.artist.id " +
            "WHERE aa.isAvailable = true " +
            "ORDER BY a.name ASC")
    List<Artist> findArtistsWithAvailableArticles();

    /**
     * Search artists by name containing
     */
    List<Artist> findByNameContainingIgnoreCaseOrderByNameAsc(String name);
}