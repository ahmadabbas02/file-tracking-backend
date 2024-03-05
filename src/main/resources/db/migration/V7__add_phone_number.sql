ALTER TABLE _user
    ADD phone_number VARCHAR(255);

UPDATE _user
SET phone_number = ''
WHERE phone_number IS NULL;
ALTER TABLE _user
    ALTER COLUMN phone_number SET NOT NULL;

UPDATE document
SET number_of_working_days = '0'
WHERE number_of_working_days IS NULL;
ALTER TABLE document
    ALTER COLUMN number_of_working_days SET NOT NULL;