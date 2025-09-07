package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.VinylCategory;
import java.util.Optional;

/**
 * Repository interface for VinylCategory entity operations
 */
@Repository
public interface VinylCategoryRepository extends JpaRepository<VinylCategory, Integer> {

    /**
     * Find vinyl category by size
     */
    Optional<VinylCategory> findBySize(String size);

    /**
     * Check if vinyl category size exists
     */
    boolean existsBySize(String size);
}