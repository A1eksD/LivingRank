CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255),
    auth_provider VARCHAR(20) NOT NULL DEFAULT 'LOCAL',
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    profile_image_url VARCHAR(500),
    last_login_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_users_email ON users(email);
