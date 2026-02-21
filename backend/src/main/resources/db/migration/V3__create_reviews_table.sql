CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    street_id BIGINT NOT NULL REFERENCES streets(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    overall_rating SMALLINT NOT NULL CHECK (overall_rating BETWEEN 1 AND 5),
    damp_in_house SMALLINT CHECK (damp_in_house BETWEEN 1 AND 5),
    friendly_neighbors SMALLINT CHECK (friendly_neighbors BETWEEN 1 AND 5),
    house_condition SMALLINT CHECK (house_condition BETWEEN 1 AND 5),
    infrastructure_connections SMALLINT CHECK (infrastructure_connections BETWEEN 1 AND 5),
    neighbors_in_general SMALLINT CHECK (neighbors_in_general BETWEEN 1 AND 5),
    neighbors_volume SMALLINT CHECK (neighbors_volume BETWEEN 1 AND 5),
    smells_bad SMALLINT CHECK (smells_bad BETWEEN 1 AND 5),
    thin_walls SMALLINT CHECK (thin_walls BETWEEN 1 AND 5),
    noise_from_street SMALLINT CHECK (noise_from_street BETWEEN 1 AND 5),
    public_safety_feeling SMALLINT CHECK (public_safety_feeling BETWEEN 1 AND 5),
    cleanliness_shared_areas SMALLINT CHECK (cleanliness_shared_areas BETWEEN 1 AND 5),
    parking_situation SMALLINT CHECK (parking_situation BETWEEN 1 AND 5),
    public_transport_access SMALLINT CHECK (public_transport_access BETWEEN 1 AND 5),
    internet_quality SMALLINT CHECK (internet_quality BETWEEN 1 AND 5),
    pest_issues SMALLINT CHECK (pest_issues BETWEEN 1 AND 5),
    heating_reliability SMALLINT CHECK (heating_reliability BETWEEN 1 AND 5),
    water_pressure_or_quality SMALLINT CHECK (water_pressure_or_quality BETWEEN 1 AND 5),
    value_for_money SMALLINT CHECK (value_for_money BETWEEN 1 AND 5),
    comment VARCHAR(2000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_reviews_street_user ON reviews(street_id, user_id);
CREATE INDEX idx_reviews_street ON reviews(street_id);
CREATE INDEX idx_reviews_user ON reviews(user_id);
