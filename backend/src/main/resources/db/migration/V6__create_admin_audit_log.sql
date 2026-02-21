CREATE TABLE admin_audit_log (
    id            BIGSERIAL PRIMARY KEY,
    admin_id      UUID NOT NULL REFERENCES users(id),
    action        VARCHAR(50) NOT NULL,
    target_type   VARCHAR(30) NOT NULL,
    target_id     VARCHAR(255) NOT NULL,
    details       TEXT,
    ip_address    VARCHAR(45),
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_admin ON admin_audit_log(admin_id);
CREATE INDEX idx_audit_target ON admin_audit_log(target_type, target_id);
CREATE INDEX idx_audit_action ON admin_audit_log(action);
CREATE INDEX idx_audit_created ON admin_audit_log(created_at DESC);
