CREATE SEQUENCE IF NOT EXISTS _user_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS category_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS category_permission_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS comment_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS token_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE _user
(
    id           BIGINT                  NOT NULL,
    email        VARCHAR(255)            NOT NULL,
    password     VARCHAR(255)            NOT NULL,
    picture      VARCHAR(255)            NOT NULL,
    phone_number VARCHAR(255) DEFAULT '' NOT NULL,
    is_enabled   BOOLEAN                 NOT NULL,
    first_name   VARCHAR(255)            NOT NULL,
    last_name    VARCHAR(255)            NOT NULL,
    CONSTRAINT pk__user PRIMARY KEY (id)
);

CREATE TABLE advisor
(
    id         VARCHAR(255) NOT NULL,
    user_id    BIGINT,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_advisor PRIMARY KEY (id)
);

CREATE TABLE category
(
    parent_category_id BIGINT       NOT NULL,
    category_id        BIGINT       NOT NULL,
    name               VARCHAR(255) NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY (parent_category_id, category_id)
);

CREATE TABLE category_permission
(
    id                          BIGINT NOT NULL,
    role                        VARCHAR(255),
    category_parent_category_id BIGINT,
    category_category_id        BIGINT,
    CONSTRAINT pk_categorypermission PRIMARY KEY (id)
);

CREATE TABLE comment
(
    id          BIGINT NOT NULL,
    message     VARCHAR(255),
    user_id     BIGINT,
    document_id UUID,
    posted_at   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_comment PRIMARY KEY (id)
);

CREATE TABLE contact_document
(
    id                     UUID NOT NULL,
    email                  VARCHAR(255),
    phone_number           VARCHAR(255),
    emergency_name         VARCHAR(255),
    emergency_phone_number VARCHAR(255),
    CONSTRAINT pk_contactdocument PRIMARY KEY (id)
);

CREATE TABLE document
(
    id                          UUID         NOT NULL,
    title                       VARCHAR(255),
    description                 VARCHAR(255),
    path                        VARCHAR(255) NOT NULL,
    student_id                  VARCHAR(255) NOT NULL,
    uploaded_at                 TIMESTAMP WITHOUT TIME ZONE,
    category_parent_category_id BIGINT,
    category_category_id        BIGINT,
    CONSTRAINT pk_document PRIMARY KEY (id)
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

CREATE TABLE student
(
    id                           VARCHAR(255) NOT NULL,
    program                      VARCHAR(255) NOT NULL,
    year                         SMALLINT     NOT NULL,
    user_id                      BIGINT       NOT NULL,
    advisor_id                   VARCHAR(255),
    internship_completion_status VARCHAR(255) NOT NULL,
    payment_status               VARCHAR(255) NOT NULL,
    created_at                   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_student PRIMARY KEY (id)
);

CREATE TABLE token
(
    id      BIGINT       NOT NULL,
    token   VARCHAR(500) NOT NULL,
    expired BOOLEAN      NOT NULL,
    blocked BOOLEAN      NOT NULL,
    user_id BIGINT,
    CONSTRAINT pk_token PRIMARY KEY (id)
);

CREATE TABLE user_roles
(
    user_id BIGINT       NOT NULL,
    role    VARCHAR(255) NOT NULL
);

ALTER TABLE _user
    ADD CONSTRAINT uc__user_email UNIQUE (email);

ALTER TABLE advisor
    ADD CONSTRAINT uc_advisor_user UNIQUE (user_id);

ALTER TABLE category
    ADD CONSTRAINT uc_category_name UNIQUE (name);

ALTER TABLE student
    ADD CONSTRAINT uc_student_user UNIQUE (user_id);

ALTER TABLE token
    ADD CONSTRAINT uc_token_token UNIQUE (token);

ALTER TABLE advisor
    ADD CONSTRAINT FK_ADVISOR_ON_USER FOREIGN KEY (user_id) REFERENCES _user (id);

ALTER TABLE category_permission
    ADD CONSTRAINT FK_CATEGORYPERMISSION_ON_CAPACACA FOREIGN KEY (category_parent_category_id, category_category_id) REFERENCES category (parent_category_id, category_id);

ALTER TABLE comment
    ADD CONSTRAINT FK_COMMENT_ON_DOCUMENT FOREIGN KEY (document_id) REFERENCES document (id);

ALTER TABLE comment
    ADD CONSTRAINT FK_COMMENT_ON_USER FOREIGN KEY (user_id) REFERENCES _user (id);

ALTER TABLE contact_document
    ADD CONSTRAINT FK_CONTACTDOCUMENT_ON_ID FOREIGN KEY (id) REFERENCES document (id);

ALTER TABLE document
    ADD CONSTRAINT FK_DOCUMENT_ON_CAPACACA FOREIGN KEY (category_parent_category_id, category_category_id) REFERENCES category (parent_category_id, category_id);

ALTER TABLE document
    ADD CONSTRAINT FK_DOCUMENT_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student (id);

ALTER TABLE internship_document
    ADD CONSTRAINT FK_INTERNSHIPDOCUMENT_ON_ID FOREIGN KEY (id) REFERENCES document (id);

ALTER TABLE medical_report_document
    ADD CONSTRAINT FK_MEDICALREPORTDOCUMENT_ON_ID FOREIGN KEY (id) REFERENCES document (id);

ALTER TABLE petition_document
    ADD CONSTRAINT FK_PETITIONDOCUMENT_ON_ID FOREIGN KEY (id) REFERENCES document (id);

ALTER TABLE student
    ADD CONSTRAINT FK_STUDENT_ON_ADVISOR FOREIGN KEY (advisor_id) REFERENCES advisor (id);

ALTER TABLE student
    ADD CONSTRAINT FK_STUDENT_ON_USER FOREIGN KEY (user_id) REFERENCES _user (id);

ALTER TABLE token
    ADD CONSTRAINT FK_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES _user (id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_roles_on_user FOREIGN KEY (user_id) REFERENCES _user (id);