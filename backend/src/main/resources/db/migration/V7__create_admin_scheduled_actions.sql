CREATE TABLE admin_scheduled_actions (
    id              BIGSERIAL PRIMARY KEY,
    admin_id        UUID NOT NULL REFERENCES users(id),
    target_user_id  UUID NOT NULL REFERENCES users(id),
    action_type     VARCHAR(30) NOT NULL,
    reason          VARCHAR(500),
    deadline        TIMESTAMP NOT NULL,
    executed        BOOLEAN NOT NULL DEFAULT FALSE,
    executed_at     TIMESTAMP,
    cancelled       BOOLEAN NOT NULL DEFAULT FALSE,
    cancelled_at    TIMESTAMP,
    related_mail_id BIGINT,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_scheduled_pending ON admin_scheduled_actions(deadline)
    WHERE executed = FALSE AND cancelled = FALSE;
CREATE INDEX idx_scheduled_target ON admin_scheduled_actions(target_user_id);
CREATE INDEX idx_scheduled_admin ON admin_scheduled_actions(admin_id);
