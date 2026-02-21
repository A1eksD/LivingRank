CREATE TABLE streets (
    id BIGSERIAL PRIMARY KEY,
    street_name VARCHAR(255) NOT NULL,
    postal_code VARCHAR(20),
    city VARCHAR(255) NOT NULL,
    state_region VARCHAR(255),
    country VARCHAR(100) NOT NULL DEFAULT 'DE',
    lat DOUBLE PRECISION,
    lon DOUBLE PRECISION,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_streets_unique ON streets(street_name, postal_code, city, country);
CREATE INDEX idx_streets_city ON streets(city);
CREATE INDEX idx_streets_search ON streets(street_name, city);
