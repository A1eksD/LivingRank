package com.livingrank.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"street_id", "user_id"})
})
@EntityListeners(AuditingEntityListener.class)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "street_id", nullable = false)
    private Street street;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "overall_rating", nullable = false)
    private Integer overallRating;

    @Column(name = "damp_in_house")
    private Integer dampInHouse;

    @Column(name = "friendly_neighbors")
    private Integer friendlyNeighbors;

    @Column(name = "house_condition")
    private Integer houseCondition;

    @Column(name = "infrastructure_connections")
    private Integer infrastructureConnections;

    @Column(name = "neighbors_in_general")
    private Integer neighborsInGeneral;

    @Column(name = "neighbors_volume")
    private Integer neighborsVolume;

    @Column(name = "smells_bad")
    private Integer smellsBad;

    @Column(name = "thin_walls")
    private Integer thinWalls;

    @Column(name = "noise_from_street")
    private Integer noiseFromStreet;

    @Column(name = "public_safety_feeling")
    private Integer publicSafetyFeeling;

    @Column(name = "cleanliness_shared_areas")
    private Integer cleanlinessSharedAreas;

    @Column(name = "parking_situation")
    private Integer parkingSituation;

    @Column(name = "public_transport_access")
    private Integer publicTransportAccess;

    @Column(name = "internet_quality")
    private Integer internetQuality;

    @Column(name = "pest_issues")
    private Integer pestIssues;

    @Column(name = "heating_reliability")
    private Integer heatingReliability;

    @Column(name = "water_pressure_or_quality")
    private Integer waterPressureOrQuality;

    @Column(name = "value_for_money")
    private Integer valueForMoney;

    @Column(length = 2000)
    private String comment;

    @Column(nullable = false)
    private boolean visible = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Review() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Street getStreet() { return street; }
    public void setStreet(Street street) { this.street = street; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Integer getOverallRating() { return overallRating; }
    public void setOverallRating(Integer overallRating) { this.overallRating = overallRating; }

    public Integer getDampInHouse() { return dampInHouse; }
    public void setDampInHouse(Integer v) { this.dampInHouse = v; }

    public Integer getFriendlyNeighbors() { return friendlyNeighbors; }
    public void setFriendlyNeighbors(Integer v) { this.friendlyNeighbors = v; }

    public Integer getHouseCondition() { return houseCondition; }
    public void setHouseCondition(Integer v) { this.houseCondition = v; }

    public Integer getInfrastructureConnections() { return infrastructureConnections; }
    public void setInfrastructureConnections(Integer v) { this.infrastructureConnections = v; }

    public Integer getNeighborsInGeneral() { return neighborsInGeneral; }
    public void setNeighborsInGeneral(Integer v) { this.neighborsInGeneral = v; }

    public Integer getNeighborsVolume() { return neighborsVolume; }
    public void setNeighborsVolume(Integer v) { this.neighborsVolume = v; }

    public Integer getSmellsBad() { return smellsBad; }
    public void setSmellsBad(Integer v) { this.smellsBad = v; }

    public Integer getThinWalls() { return thinWalls; }
    public void setThinWalls(Integer v) { this.thinWalls = v; }

    public Integer getNoiseFromStreet() { return noiseFromStreet; }
    public void setNoiseFromStreet(Integer v) { this.noiseFromStreet = v; }

    public Integer getPublicSafetyFeeling() { return publicSafetyFeeling; }
    public void setPublicSafetyFeeling(Integer v) { this.publicSafetyFeeling = v; }

    public Integer getCleanlinessSharedAreas() { return cleanlinessSharedAreas; }
    public void setCleanlinessSharedAreas(Integer v) { this.cleanlinessSharedAreas = v; }

    public Integer getParkingSituation() { return parkingSituation; }
    public void setParkingSituation(Integer v) { this.parkingSituation = v; }

    public Integer getPublicTransportAccess() { return publicTransportAccess; }
    public void setPublicTransportAccess(Integer v) { this.publicTransportAccess = v; }

    public Integer getInternetQuality() { return internetQuality; }
    public void setInternetQuality(Integer v) { this.internetQuality = v; }

    public Integer getPestIssues() { return pestIssues; }
    public void setPestIssues(Integer v) { this.pestIssues = v; }

    public Integer getHeatingReliability() { return heatingReliability; }
    public void setHeatingReliability(Integer v) { this.heatingReliability = v; }

    public Integer getWaterPressureOrQuality() { return waterPressureOrQuality; }
    public void setWaterPressureOrQuality(Integer v) { this.waterPressureOrQuality = v; }

    public Integer getValueForMoney() { return valueForMoney; }
    public void setValueForMoney(Integer v) { this.valueForMoney = v; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
