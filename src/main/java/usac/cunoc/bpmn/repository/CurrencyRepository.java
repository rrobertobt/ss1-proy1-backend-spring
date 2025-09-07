package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.Currency;
import java.util.Optional;

/**
 * Repository interface for Currency entity operations
 */
@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Integer> {

    /**
     * Find currency by code
     */
    Optional<Currency> findByCode(String code);

    /**
     * Find currency by name
     */
    Optional<Currency> findByName(String name);

    /**
     * Check if currency code exists
     */
    boolean existsByCode(String code);
}