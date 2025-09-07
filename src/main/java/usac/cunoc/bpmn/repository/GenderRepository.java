package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.Gender;

/**
 * Repository interface for Gender entity operations
 */
@Repository
public interface GenderRepository extends JpaRepository<Gender, Integer> {
}