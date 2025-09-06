package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import usac.cunoc.bpmn.entity.User;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository interface for User entity operations with database constraints
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Find user by username respecting database case sensitivity
     */
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    /**
     * Find user by email respecting database case sensitivity
     */
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * Find user by username or email for login
     */
    @Query("SELECT u FROM User u WHERE u.username = :login OR u.email = :login")
    Optional<User> findByUsernameOrEmail(@Param("login") String login);

    /**
     * Check if username exists (case sensitive)
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username")
    boolean existsByUsername(@Param("username") String username);

    /**
     * Check if email exists (case insensitive for email)
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    boolean existsByEmail(@Param("email") String email);

    /**
     * Find user by email and valid two factor code matching database constraints
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.twoFactorCode = :code " +
            "AND u.twoFactorCodeExpires > :now")
    Optional<User> findByEmailAndValidTwoFactorCode(@Param("email") String email,
            @Param("code") String code,
            @Param("now") LocalDateTime now);

    /**
     * Update last login timestamp
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastLogin = :loginTime, u.updatedAt = :updateTime WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") Integer userId,
            @Param("loginTime") LocalDateTime loginTime,
            @Param("updateTime") LocalDateTime updateTime);

    /**
     * Find users with failed login attempts for security monitoring
     */
    @Query("SELECT u FROM User u WHERE u.failedLoginAttempts >= :attempts")
    java.util.List<User> findUsersWithFailedAttempts(@Param("attempts") Integer attempts);

    /**
     * Find locked users
     */
    @Query("SELECT u FROM User u WHERE u.lockedUntil IS NOT NULL AND u.lockedUntil > :now")
    java.util.List<User> findLockedUsers(@Param("now") LocalDateTime now);
}