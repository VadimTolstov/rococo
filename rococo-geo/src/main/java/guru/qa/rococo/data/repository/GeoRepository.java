package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.GeoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GeoRepository extends JpaRepository<GeoEntity, UUID> {
    Page<GeoEntity> findByCityContainingIgnoreCase(Pageable pageable, String city);

    List<GeoEntity> findByCityAndCountryName(String city, String countryName);

    @Query("SELECT g FROM GeoEntity g WHERE LOWER(g.city) = LOWER(:city) AND g.country.id = :countryId")
    Optional<GeoEntity> findByCityAndCountryId(@Param("city") String city, @Param("countryId") UUID countryId);
}
