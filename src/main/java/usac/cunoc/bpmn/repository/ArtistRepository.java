package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.Artist;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Artist entity operations
 */
@Repository
public interface ArtistRepository extends JpaRepository<Artist, Integer> {

    /**
     * Find artist by name (case insensitive)
     */
    Optional<Artist> findByNameIgnoreCase(String name);

    /**
     * Check if artist name exists (case insensitive)
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Check if artist name exists excluding specific ID (for updates)
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Integer id);

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

    /**
     * Find artist by ID with country relationship loaded
     */
    @Query("SELECT a FROM Artist a LEFT JOIN FETCH a.country WHERE a.id = :id")
    Optional<Artist> findByIdWithCountry(Integer id);

    /**
     * Find all artists ordered by name
     */
    List<Artist> findAllByOrderByNameAsc();

    /**
     * Find artists by country ID
     */
    List<Artist> findByCountryIdOrderByNameAsc(Integer countryId);

    /**
     * Check if any articles exist for this artist
     */
    @Query("SELECT COUNT(aa) > 0 FROM AnalogArticle aa WHERE aa.artist.id = :artistId")
    boolean hasArticles(Integer artistId);
}