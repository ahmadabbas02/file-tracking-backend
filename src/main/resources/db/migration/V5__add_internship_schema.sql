ALTER TABLE student
    ADD internship_completion_status VARCHAR(255);

ALTER TABLE student
    ADD payment_status VARCHAR(255);

UPDATE student
SET internship_completion_status = 'INCOMPLETE'
WHERE internship_completion_status IS NULL;
ALTER TABLE student
    ALTER COLUMN internship_completion_status SET NOT NULL;

ALTER TABLE document
    ADD number_of_working_days INTEGER;

UPDATE student
SET payment_status = 'NOT_PAID'
WHERE payment_status IS NULL;
ALTER TABLE student
    ALTER COLUMN payment_status SET NOT NULL;