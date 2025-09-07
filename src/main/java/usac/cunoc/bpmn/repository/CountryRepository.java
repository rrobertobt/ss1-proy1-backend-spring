package usac.cunoc.bpmn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import usac.cunoc.bpmn.entity.Country;
import java.util.Optional;

/**
 * Repository interface for Country entity operations
 */
@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {

    /**
     * Find country by name
     */
    Optional<Country> findByName(String name);

    /**
     * Find country by country code
     */
    Optional<Country> findByCountryCode(String countryCode);

    /**
     * Check if country name exists
     */
    boolean existsByName(String name);

    /**
     * Check if country code exists
     */
    boolean existsByCountryCode(String countryCode);
}