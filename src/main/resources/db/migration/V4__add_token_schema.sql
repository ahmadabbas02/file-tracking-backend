CREATE SEQUENCE IF NOT EXISTS token_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE token
(
    id      BIGINT       NOT NULL,
    token   VARCHAR(255) NOT NULL,
    expired BOOLEAN      NOT NULL,
    blocked BOOLEAN      NOT NULL,
    user_id BIGINT,
    CONSTRAINT pk_token PRIMARY KEY (id)
);

ALTER TABLE token
    ADD CONSTRAINT uc_token_token UNIQUE (token);

ALTER TABLE token
    ADD CONSTRAINT FK_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES _user (id);