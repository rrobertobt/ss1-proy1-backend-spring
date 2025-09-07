package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.VinylSpecialEdition;
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
     * Check if vinyl special edition name exists
     */
    boolean existsByName(String name);
}