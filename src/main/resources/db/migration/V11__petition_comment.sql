CREATE SEQUENCE IF NOT EXISTS comment_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE comment
(
    id          BIGINT NOT NULL,
    message     VARCHAR(255),
    user_id     BIGINT,
    document_id UUID,
    posted_at   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_comment PRIMARY KEY (id)
);

ALTER TABLE document
    ADD is_approved BOOLEAN;

UPDATE document
SET is_approved = 'FALSE'
WHERE is_approved IS NULL;
ALTER TABLE document
    ALTER COLUMN is_approved SET NOT NULL;

ALTER TABLE comment
    ADD CONSTRAINT FK_COMMENT_ON_DOCUMENT FOREIGN KEY (document_id) REFERENCES document (id);

ALTER TABLE comment
    ADD CONSTRAINT FK_COMMENT_ON_USER FOREIGN KEY (user_id) REFERENCES _user (id);