ALTER TABLE student
    ADD education_status VARCHAR(255);

UPDATE student
SET education_status = 'UNDERGRADUATE'
WHERE education_status IS NULL;
ALTER TABLE student
    ALTER COLUMN education_status SET NOT NULL;