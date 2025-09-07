package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import usac.cunoc.bpmn.entity.CreditCard;
import usac.cunoc.bpmn.entity.User;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CreditCard entity operations
 */
@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Integer> {

    /**
     * Find all active cards by user
     */
    List<CreditCard> findByUserAndIsActiveTrueOrderByCreatedAtDesc(User user);

    /**
     * Find user's default card
     */
    Optional<CreditCard> findByUserAndIsDefaultTrueAndIsActiveTrue(User user);

    /**
     * Find card by user and id
     */
    Optional<CreditCard> findByIdAndUserAndIsActiveTrue(Integer id, User user);

    /**
     * Clear all default flags for user before setting new default
     */
    @Modifying
    @Transactional
    @Query("UPDATE CreditCard cc SET cc.isDefault = false WHERE cc.user = :user")
    void clearDefaultFlags(@Param("user") User user);

    /**
     * Count active cards by user
     */
    long countByUserAndIsActiveTrue(User user);

    /**
     * Check if last four digits already exist for user (to prevent duplicates)
     */
    boolean existsByUserAndLastFourDigitsAndIsActiveTrue(User user, String lastFourDigits);
}