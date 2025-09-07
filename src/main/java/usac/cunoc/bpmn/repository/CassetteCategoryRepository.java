package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.CassetteCategory;
import java.util.Optional;

/**
 * Repository interface for CassetteCategory entity operations
 */
@Repository
public interface CassetteCategoryRepository extends JpaRepository<CassetteCategory, Integer> {

    /**
     * Find cassette category by name
     */
    Optional<CassetteCategory> findByName(String name);

    /**
     * Check if cassette category name exists
     */
    boolean existsByName(String name);
}