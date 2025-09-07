package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.CardBrand;
import java.util.Optional;

/**
 * Repository interface for CardBrand entity operations
 */
@Repository
public interface CardBrandRepository extends JpaRepository<CardBrand, Integer> {

    /**
     * Find card brand by name
     */
    Optional<CardBrand> findByName(String name);
}