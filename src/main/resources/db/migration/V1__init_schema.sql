CREATE SEQUENCE IF NOT EXISTS _user_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS category_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS category_permission_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE _user
(
    id         BIGINT       NOT NULL,
    name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    picture    VARCHAR(255) NOT NULL,
    is_enabled BOOLEAN      NOT NULL,
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
    parent_category_id BIGINT NOT NULL,
    category_id        BIGINT NOT NULL,
    name               VARCHAR(255),
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

CREATE TABLE document
(
    id                          UUID         NOT NULL,
    dtype                       VARCHAR(31),
    title                       VARCHAR(255),
    description                 VARCHAR(255),
    path                        VARCHAR(255) NOT NULL,
    student_id                  VARCHAR(255) NOT NULL,
    uploaded_at                 TIMESTAMP WITHOUT TIME ZONE,
    email                       VARCHAR(255),
    phone_number                VARCHAR(255),
    emergency_name              VARCHAR(255),
    emergency_phone_number      VARCHAR(255),
    date_of_absence             TIMESTAMP WITHOUT TIME ZONE,
    note                        VARCHAR(255),
    medical_report_status       VARCHAR(255),
    category_parent_category_id BIGINT,
    category_category_id        BIGINT,
    CONSTRAINT pk_document PRIMARY KEY (id)
);

CREATE TABLE student
(
    id         VARCHAR(255) NOT NULL,
    department VARCHAR(255) NOT NULL,
    year       SMALLINT     NOT NULL,
    user_id    BIGINT       NOT NULL,
    advisor_id VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_student PRIMARY KEY (id)
);

CREATE TABLE user_roles
(
    user_id BIGINT       NOT NULL,
    role    VARCHAR(255) NOT NULL
);

ALTER TABLE _user
    ADD CONSTRAINT uc__user_email UNIQUE (email);

ALTER TABLE _user
    ADD CONSTRAINT uc__user_name UNIQUE (name);

ALTER TABLE advisor
    ADD CONSTRAINT uc_advisor_user UNIQUE (user_id);

ALTER TABLE student
    ADD CONSTRAINT uc_student_user UNIQUE (user_id);

CREATE INDEX idx_user_name ON _user (name);

ALTER TABLE advisor
    ADD CONSTRAINT FK_ADVISOR_ON_USER FOREIGN KEY (user_id) REFERENCES _user (id);

ALTER TABLE category_permission
    ADD CONSTRAINT FK_CATEGORYPERMISSION_ON_CAPACAIDCACAID FOREIGN KEY (category_parent_category_id, category_category_id) REFERENCES category (parent_category_id, category_id);

ALTER TABLE document
    ADD CONSTRAINT FK_DOCUMENT_ON_CAPACAIDCACAID FOREIGN KEY (category_parent_category_id, category_category_id) REFERENCES category (parent_category_id, category_id);

ALTER TABLE document
    ADD CONSTRAINT FK_DOCUMENT_ON_STUDENT FOREIGN KEY (student_id) REFERENCES student (id);

ALTER TABLE student
    ADD CONSTRAINT FK_STUDENT_ON_ADVISOR FOREIGN KEY (advisor_id) REFERENCES advisor (id);

ALTER TABLE student
    ADD CONSTRAINT FK_STUDENT_ON_USER FOREIGN KEY (user_id) REFERENCES _user (id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_roles_on_user FOREIGN KEY (user_id) REFERENCES _user (id);