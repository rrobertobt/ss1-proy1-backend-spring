package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.MovementReferenceType;
import java.util.Optional;

/**
 * Repository interface for MovementReferenceType entity operations
 */
@Repository
public interface MovementReferenceTypeRepository extends JpaRepository<MovementReferenceType, Integer> {

    /**
     * Find movement reference type by name
     */
    Optional<MovementReferenceType> findByName(String name);

    /**
     * Check if movement reference type name exists
     */
    boolean existsByName(String name);
}