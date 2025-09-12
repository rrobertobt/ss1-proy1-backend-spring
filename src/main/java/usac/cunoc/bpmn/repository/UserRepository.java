package usac.cunoc.bpmn.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
         * Fetches userType to avoid LazyInitializationException in security context
         */
        @Query("SELECT u FROM User u JOIN FETCH u.userType WHERE u.username = :login OR u.email = :login")
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

        /**
         * Search users by multiple fields for admin panel
         */
        @Query("SELECT u FROM User u WHERE " +
                        "LOWER(u.username) LIKE LOWER(:search) OR " +
                        "LOWER(u.email) LIKE LOWER(:search) OR " +
                        "LOWER(u.firstName) LIKE LOWER(:search) OR " +
                        "LOWER(u.lastName) LIKE LOWER(:search)")
        Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                        @Param("search") String search1, @Param("search") String search2,
                        @Param("search") String search3, @Param("search") String search4,
                        Pageable pageable);

        /**
         * Find users by user type
         */
        @Query("SELECT u FROM User u WHERE u.userType.name = :userTypeName")
        Page<User> findByUserTypeName(@Param("userTypeName") String userTypeName, Pageable pageable);

        /**
         * Find users by status
         */
        Page<User> findByIsActiveAndIsBanned(Boolean isActive, Boolean isBanned, Pageable pageable);
}
