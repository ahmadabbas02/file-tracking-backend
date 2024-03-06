ALTER TABLE document
    ALTER COLUMN number_of_working_days DROP NOT NULL;

ALTER TABLE document
    ALTER COLUMN number_of_working_days SET DEFAULT 0;