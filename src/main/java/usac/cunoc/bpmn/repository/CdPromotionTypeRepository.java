package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.CdPromotionType;
import java.util.Optional;

/**
 * CD promotion type repository interface
 */
@Repository
public interface CdPromotionTypeRepository extends JpaRepository<CdPromotionType, Integer> {

    /**
     * Find promotion type by name
     */
    @Query("SELECT cpt FROM CdPromotionType cpt WHERE cpt.name = :name")
    Optional<CdPromotionType> findByName(@Param("name") String name);

    /**
     * Check if promotion type exists by name
     */
    @Query("SELECT COUNT(cpt) > 0 FROM CdPromotionType cpt WHERE cpt.name = :name")
    boolean existsByName(@Param("name") String name);
}