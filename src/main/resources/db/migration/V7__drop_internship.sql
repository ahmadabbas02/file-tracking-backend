ALTER TABLE internship_document
    DROP CONSTRAINT fk_internshipdocument_on_id;

DROP TABLE internship_document CASCADE;