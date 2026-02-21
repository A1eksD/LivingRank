package com.livingrank.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "streets", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"street_name", "postal_code", "city", "country"})
})
@EntityListeners(AuditingEntityListener.class)
public class Street {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "street_name", nullable = false)
    private String streetName;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(nullable = false)
    private String city;

    @Column(name = "state_region")
    private String stateRegion;

    @Column(nullable = false, length = 100)
    private String country = "DE";

    private Double lat;
    private Double lon;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Street() {}

    public Street(String streetName, String postalCode, String city, String stateRegion, String country, Double lat, Double lon) {
        this.streetName = streetName;
        this.postalCode = postalCode;
        this.city = city;
        this.stateRegion = stateRegion;
        this.country = country;
        this.lat = lat;
        this.lon = lon;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStreetName() { return streetName; }
    public void setStreetName(String streetName) { this.streetName = streetName; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getStateRegion() { return stateRegion; }
    public void setStateRegion(String stateRegion) { this.stateRegion = stateRegion; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    public Double getLon() { return lon; }
    public void setLon(Double lon) { this.lon = lon; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
