ALTER TABLE student
    RENAME COLUMN department TO program;

ALTER TABLE student
    ALTER COLUMN program SET NOT NULL;