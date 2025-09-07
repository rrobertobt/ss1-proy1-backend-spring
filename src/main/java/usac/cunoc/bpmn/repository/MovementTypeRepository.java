package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.MovementType;
import java.util.Optional;

/**
 * Repository interface for MovementType entity operations
 */
@Repository
public interface MovementTypeRepository extends JpaRepository<MovementType, Integer> {

    /**
     * Find movement type by name
     */
    Optional<MovementType> findByName(String name);

    /**
     * Check if movement type name exists
     */
    boolean existsByName(String name);
}