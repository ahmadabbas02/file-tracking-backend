CREATE TABLE contact_document
(
    id                     UUID NOT NULL,
    email                  VARCHAR(255),
    phone_number           VARCHAR(255),
    emergency_name         VARCHAR(255),
    emergency_phone_number VARCHAR(255),
    CONSTRAINT pk_contactdocument PRIMARY KEY (id)
);

CREATE TABLE internship_document
(
    id                     UUID NOT NULL,
    number_of_working_days INTEGER DEFAULT 0,
    CONSTRAINT pk_internshipdocument PRIMARY KEY (id)
);

CREATE TABLE medical_report_document
(
    id              UUID NOT NULL,
    date_of_absence date,
    is_approved     BOOLEAN DEFAULT FALSE,
    CONSTRAINT pk_medicalreportdocument PRIMARY KEY (id)
);

CREATE TABLE petition_document
(
    id          UUID NOT NULL,
    subject     VARCHAR(255),
    email       VARCHAR(255),
    is_approved BOOLEAN DEFAULT FALSE,
    CONSTRAINT pk_petitiondocument PRIMARY KEY (id)
);

ALTER TABLE contact_document
    ADD CONSTRAINT FK_CONTACTDOCUMENT_ON_ID FOREIGN KEY (id) REFERENCES document (id);

ALTER TABLE internship_document
    ADD CONSTRAINT FK_INTERNSHIPDOCUMENT_ON_ID FOREIGN KEY (id) REFERENCES document (id);

ALTER TABLE medical_report_document
    ADD CONSTRAINT FK_MEDICALREPORTDOCUMENT_ON_ID FOREIGN KEY (id) REFERENCES document (id);

ALTER TABLE petition_document
    ADD CONSTRAINT FK_PETITIONDOCUMENT_ON_ID FOREIGN KEY (id) REFERENCES document (id);

ALTER TABLE document
    DROP COLUMN date_of_absence;

ALTER TABLE document
    DROP COLUMN dtype;

ALTER TABLE document
    DROP COLUMN email;

ALTER TABLE document
    DROP COLUMN emergency_name;

ALTER TABLE document
    DROP COLUMN emergency_phone_number;

ALTER TABLE document
    DROP COLUMN is_approved;

ALTER TABLE document
    DROP COLUMN number_of_working_days;

ALTER TABLE document
    DROP COLUMN phone_number;

ALTER TABLE document
    DROP COLUMN subject;