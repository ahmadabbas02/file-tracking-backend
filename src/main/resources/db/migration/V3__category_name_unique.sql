ALTER TABLE category
    ADD CONSTRAINT uc_category_name UNIQUE (name);

ALTER TABLE category
    ALTER COLUMN name SET NOT NULL;