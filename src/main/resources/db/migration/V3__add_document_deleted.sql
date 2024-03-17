ALTER TABLE document
    ADD deleted BOOLEAN;

ALTER TABLE document
    ALTER COLUMN deleted SET NOT NULL;