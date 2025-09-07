package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.CommentStatus;
import java.util.Optional;

/**
 * Repository interface for CommentStatus entity operations
 */
@Repository
public interface CommentStatusRepository extends JpaRepository<CommentStatus, Integer> {

    /**
     * Find comment status by name
     */
    Optional<CommentStatus> findByName(String name);

    /**
     * Check if comment status name exists
     */
    boolean existsByName(String name);
}