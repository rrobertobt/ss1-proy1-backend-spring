package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.User;
import java.util.Optional;

/**
 * Repository interface for User entity - exact database compliance
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

        /**
         * Find user by username - exact database case sensitivity
         */
        Optional<User> findByUsername(String username);

        /**
         * Find user by email - exact database case sensitivity
         */
        Optional<User> findByEmail(String email);

        /**
         * Find user by username or email for login - exact match
         */
        @Query("SELECT u FROM User u WHERE u.username = :login OR u.email = :login")
        Optional<User> findByUsernameOrEmail(@Param("login") String login);

        /**
         * Check if username exists - respecting UNIQUE constraint
         */
        boolean existsByUsername(String username);

        /**
         * Check if email exists - respecting UNIQUE constraint
         */
        boolean existsByEmail(String email);

        /**
         * Find user by email and valid 2FA code - exact BD field match
         */
        @Query("SELECT u FROM User u WHERE u.email = :email AND u.twoFactorCode = :code " +
                        "AND u.twoFactorCodeExpires > CURRENT_TIMESTAMP")
        Optional<User> findByEmailAndValidTwoFactorCode(@Param("email") String email,
                        @Param("code") String code);
}