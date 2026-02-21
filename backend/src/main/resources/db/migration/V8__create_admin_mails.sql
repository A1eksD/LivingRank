CREATE TABLE admin_mails (
    id              BIGSERIAL PRIMARY KEY,
    admin_id        UUID NOT NULL REFERENCES users(id),
    recipient_id    UUID NOT NULL REFERENCES users(id),
    subject         VARCHAR(255) NOT NULL,
    body            TEXT NOT NULL,
    has_deadline    BOOLEAN NOT NULL DEFAULT FALSE,
    deadline_action VARCHAR(30),
    sent_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE admin_scheduled_actions
    ADD CONSTRAINT fk_scheduled_mail
    FOREIGN KEY (related_mail_id) REFERENCES admin_mails(id);

CREATE INDEX idx_admin_mails_recipient ON admin_mails(recipient_id);
CREATE INDEX idx_admin_mails_admin ON admin_mails(admin_id);
