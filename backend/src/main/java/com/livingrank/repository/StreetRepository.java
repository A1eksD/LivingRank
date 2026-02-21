package com.livingrank.repository;

import com.livingrank.entity.Street;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StreetRepository extends JpaRepository<Street, Long> {

    @Query("SELECT s FROM Street s WHERE LOWER(s.streetName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(s.city) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY s.city, s.streetName")
    List<Street> searchByQuery(@Param("query") String query);

    Optional<Street> findByStreetNameAndPostalCodeAndCityAndCountry(
        String streetName, String postalCode, String city, String country);
}
