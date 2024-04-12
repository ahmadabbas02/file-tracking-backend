CREATE SEQUENCE IF NOT EXISTS activation_code_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE activation_code
(
    id         INTEGER NOT NULL,
    code       VARCHAR(12),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    expires_at TIMESTAMP WITHOUT TIME ZONE,
    user_id    BIGINT  NOT NULL,
    CONSTRAINT pk_activationcode PRIMARY KEY (id)
);

ALTER TABLE activation_code
    ADD CONSTRAINT FK_ACTIVATIONCODE_ON_USER FOREIGN KEY (user_id) REFERENCES _user (id);
