DROP TABLE notifications;

CREATE TABLE IF NOT EXISTS notifications
(
    id              BIGSERIAL    PRIMARY KEY,
    recipient       VARCHAR(255) NOT NULL,
    message         TEXT         NOT NULL,
    status          VARCHAR(16)  NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL,
    sent_at         TIMESTAMPTZ,
    send_count      INT          NOT NULL DEFAULT 0
);