ALTER TABLE category
    ADD deleted BOOLEAN;

UPDATE category
SET deleted = 'FALSE'
WHERE deleted IS NULL;
ALTER TABLE category
    ALTER COLUMN deleted SET NOT NULL;