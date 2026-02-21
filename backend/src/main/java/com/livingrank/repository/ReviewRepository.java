package com.livingrank.repository;

import com.livingrank.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByStreetIdOrderByCreatedAtDesc(Long streetId, Pageable pageable);

    Page<Review> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    boolean existsByStreetIdAndUserId(Long streetId, UUID userId);

    Optional<Review> findByIdAndUserId(Long id, UUID userId);

    @Query("SELECT AVG(r.overallRating) FROM Review r WHERE r.street.id = :streetId")
    Double getAverageOverallRating(@Param("streetId") Long streetId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.street.id = :streetId")
    Long getReviewCount(@Param("streetId") Long streetId);

    @Query("SELECT AVG(r.dampInHouse), AVG(r.friendlyNeighbors), AVG(r.houseCondition), " +
           "AVG(r.infrastructureConnections), AVG(r.neighborsInGeneral), AVG(r.neighborsVolume), " +
           "AVG(r.smellsBad), AVG(r.thinWalls), AVG(r.noiseFromStreet), AVG(r.publicSafetyFeeling), " +
           "AVG(r.cleanlinessSharedAreas), AVG(r.parkingSituation), AVG(r.publicTransportAccess), " +
           "AVG(r.internetQuality), AVG(r.pestIssues), AVG(r.heatingReliability), " +
           "AVG(r.waterPressureOrQuality), AVG(r.valueForMoney) " +
           "FROM Review r WHERE r.street.id = :streetId")
    List<Object[]> getCriteriaAverages(@Param("streetId") Long streetId);

    Page<Review> findByStreetIdAndVisible(Long streetId, boolean visible, Pageable pageable);
    Page<Review> findByVisible(boolean visible, Pageable pageable);
    Page<Review> findAllByOrderByCreatedAtDesc(Pageable pageable);
    long countByVisible(boolean visible);

    @Modifying
    @Query("UPDATE Review r SET r.visible = false WHERE r.user.id = :userId")
    void hideAllByUserId(@Param("userId") UUID userId);
}
