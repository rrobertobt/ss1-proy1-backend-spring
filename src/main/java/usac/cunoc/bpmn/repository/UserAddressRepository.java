package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import usac.cunoc.bpmn.entity.User;
import usac.cunoc.bpmn.entity.UserAddress;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for UserAddress entity operations
 */
@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Integer> {

    /**
     * Find all addresses by user
     */
    List<UserAddress> findByUserOrderByCreatedAtDesc(User user);

    /**
     * Find user's default address
     */
    Optional<UserAddress> findByUserAndIsDefaultTrue(User user);

    /**
     * Find user's default billing address
     */
    Optional<UserAddress> findByUserAndIsBillingDefaultTrue(User user);

    /**
     * Find user's default shipping address
     */
    Optional<UserAddress> findByUserAndIsShippingDefaultTrue(User user);

    /**
     * Find address by user and id
     */
    Optional<UserAddress> findByIdAndUser(Integer id, User user);

    /**
     * Clear all default flags for user before setting new default
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserAddress ua SET ua.isDefault = false WHERE ua.user = :user")
    void clearDefaultFlags(@Param("user") User user);

    /**
     * Clear all billing default flags for user
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserAddress ua SET ua.isBillingDefault = false WHERE ua.user = :user")
    void clearBillingDefaultFlags(@Param("user") User user);

    /**
     * Clear all shipping default flags for user
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserAddress ua SET ua.isShippingDefault = false WHERE ua.user = :user")
    void clearShippingDefaultFlags(@Param("user") User user);

    /**
     * Count addresses by user
     */
    long countByUser(User user);
}