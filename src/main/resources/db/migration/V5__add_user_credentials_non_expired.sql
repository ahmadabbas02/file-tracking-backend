ALTER TABLE _user
    ADD is_credentials_non_expired BOOLEAN;

UPDATE _user
SET is_credentials_non_expired = 'TRUE'
WHERE is_credentials_non_expired IS NULL;
ALTER TABLE _user
    ALTER COLUMN is_credentials_non_expired SET NOT NULL;