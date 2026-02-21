-- Add role and status columns to users table
ALTER TABLE users ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER';
ALTER TABLE users ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';
ALTER TABLE users ADD COLUMN suspended_at TIMESTAMP;
ALTER TABLE users ADD COLUMN suspended_reason VARCHAR(500);

CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_status ON users(status);

-- Add visibility flag to reviews
ALTER TABLE reviews ADD COLUMN visible BOOLEAN NOT NULL DEFAULT TRUE;
CREATE INDEX idx_reviews_visible ON reviews(visible);
