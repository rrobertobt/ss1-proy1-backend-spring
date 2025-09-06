package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import usac.cunoc.bpmn.entity.PasswordResetToken;
import usac.cunoc.bpmn.entity.User;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository interface for PasswordResetToken entity operations
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {

    /**
     * Find valid token by token string
     */
    @Query("SELECT t FROM PasswordResetToken t WHERE t.token = :token AND t.used = false AND t.expiresAt > :now")
    Optional<PasswordResetToken> findValidToken(@Param("token") String token, @Param("now") LocalDateTime now);

    /**
     * Delete expired tokens
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    /**
     * Delete all tokens for user
     */
    @Modifying
    @Transactional
    void deleteByUser(User user);
}