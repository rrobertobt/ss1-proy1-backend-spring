package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.VinylSpecialEdition;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for VinylSpecialEdition entity operations
 */
@Repository
public interface VinylSpecialEditionRepository extends JpaRepository<VinylSpecialEdition, Integer> {

    /**
     * Find vinyl special edition by name
     */
    Optional<VinylSpecialEdition> findByName(String name);

    /**
     * Find vinyl special edition by name (case insensitive)
     */
    Optional<VinylSpecialEdition> findByNameIgnoreCase(String name);

    /**
     * Check if vinyl special edition name exists
     */
    boolean existsByName(String name);

    /**
     * Check if vinyl special edition name exists (case insensitive)
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Check if vinyl special edition name exists excluding specific ID (for
     * updates)
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Integer id);

    /**
     * Find all limited editions
     */
    List<VinylSpecialEdition> findByIsLimitedTrueOrderByNameAsc();

    /**
     * Find all non-limited editions
     */
    List<VinylSpecialEdition> findByIsLimitedFalseOrderByNameAsc();

    /**
     * Find editions by color
     */
    List<VinylSpecialEdition> findByColorContainingIgnoreCaseOrderByNameAsc(String color);

    /**
     * Count how many vinyls use this special edition
     */
    @Query("SELECT COUNT(v) FROM Vinyl v WHERE v.vinylSpecialEdition.id = :editionId")
    long countVinylsUsingEdition(Integer editionId);

    /**
     * Find all editions ordered by name
     */
    List<VinylSpecialEdition> findAllByOrderByNameAsc();
}