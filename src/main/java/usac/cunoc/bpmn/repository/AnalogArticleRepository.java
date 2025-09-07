package usac.cunoc.bpmn.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.AnalogArticle;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * Repository interface for AnalogArticle entity operations
 */
@Repository
public interface AnalogArticleRepository extends JpaRepository<AnalogArticle, Integer> {

    /**
     * Find all available articles with pagination
     */
    Page<AnalogArticle> findByIsAvailableTrue(Pageable pageable);

    /**
     * Search articles by title or artist name
     */
    @Query("SELECT aa FROM AnalogArticle aa JOIN aa.artist a WHERE " +
            "LOWER(aa.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<AnalogArticle> findByTitleOrArtistNameContainingIgnoreCase(
            @Param("search") String search, Pageable pageable);

    /**
     * Filter articles by genre
     */
    @Query("SELECT aa FROM AnalogArticle aa WHERE aa.musicGenre.id = :genreId AND aa.isAvailable = true")
    Page<AnalogArticle> findByMusicGenreIdAndIsAvailableTrue(
            @Param("genreId") Integer genreId, Pageable pageable);

    /**
     * Filter articles by artist
     */
    @Query("SELECT aa FROM AnalogArticle aa WHERE aa.artist.id = :artistId AND aa.isAvailable = true")
    Page<AnalogArticle> findByArtistIdAndIsAvailableTrue(
            @Param("artistId") Integer artistId, Pageable pageable);

    /**
     * Filter articles by price range
     */
    @Query("SELECT aa FROM AnalogArticle aa WHERE aa.price >= :minPrice AND aa.price <= :maxPrice AND aa.isAvailable = true")
    Page<AnalogArticle> findByPriceBetweenAndIsAvailableTrue(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    /**
     * Filter articles by type (vinyl, cassette, cd)
     */
    @Query("SELECT aa FROM AnalogArticle aa WHERE aa.id IN " +
            "(SELECT v.analogArticle.id FROM Vinyl v) AND aa.isAvailable = true")
    Page<AnalogArticle> findVinylsAvailable(Pageable pageable);

    @Query("SELECT aa FROM AnalogArticle aa WHERE aa.id IN " +
            "(SELECT c.analogArticle.id FROM Cassette c) AND aa.isAvailable = true")
    Page<AnalogArticle> findCassettesAvailable(Pageable pageable);

    @Query("SELECT aa FROM AnalogArticle aa WHERE aa.id IN " +
            "(SELECT cd.analogArticle.id FROM Cd cd) AND aa.isAvailable = true")
    Page<AnalogArticle> findCdsAvailable(Pageable pageable);

    /**
     * Complex filter query
     */
    @Query("SELECT aa FROM AnalogArticle aa " +
            "LEFT JOIN aa.artist a " +
            "WHERE aa.isAvailable = true " +
            "AND (:search IS NULL OR LOWER(aa.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "     OR LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:genreId IS NULL OR aa.musicGenre.id = :genreId) " +
            "AND (:artistId IS NULL OR aa.artist.id = :artistId) " +
            "AND (:minPrice IS NULL OR aa.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR aa.price <= :maxPrice)")
    Page<AnalogArticle> findWithFilters(
            @Param("search") String search,
            @Param("genreId") Integer genreId,
            @Param("artistId") Integer artistId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    /**
     * Get price range for available articles
     */
    @Query("SELECT MIN(aa.price), MAX(aa.price) FROM AnalogArticle aa WHERE aa.isAvailable = true")
    Object[] findPriceRange();

    /**
     * Get most sold articles
     */
    @Query("SELECT aa FROM AnalogArticle aa WHERE aa.isAvailable = true ORDER BY aa.totalSold DESC")
    Page<AnalogArticle> findMostSoldArticles(Pageable pageable);

    /**
     * Check if article exists and is available
     */
    boolean existsByIdAndIsAvailableTrue(Integer id);

    /**
     * Find article with all related data for detail view
     */
    @Query("SELECT aa FROM AnalogArticle aa " +
            "LEFT JOIN FETCH aa.artist " +
            "LEFT JOIN FETCH aa.musicGenre " +
            "LEFT JOIN FETCH aa.currency " +
            "WHERE aa.id = :id")
    Optional<AnalogArticle> findByIdWithDetails(@Param("id") Integer id);
}