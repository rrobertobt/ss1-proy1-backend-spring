package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.UserType;
import java.util.Optional;

/**
 * Repository interface for UserType entity operations
 */
@Repository
public interface UserTypeRepository extends JpaRepository<UserType, Integer> {

    /**
     * Find user type by name
     */
    Optional<UserType> findByName(String name);
}