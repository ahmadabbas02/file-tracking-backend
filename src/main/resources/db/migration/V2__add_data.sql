INSERT INTO category (category_id, parent_category_id, name)
VALUES (nextval('category_seq'), -1, 'Main Cat. 1');

INSERT INTO category (category_id, parent_category_id, name)
VALUES (nextval('category_seq'), 1, 'Sub Cat. 1');

INSERT INTO category (category_id, parent_category_id, name)
VALUES (nextval('category_seq'), 1, 'Sub Cat. 2');

INSERT INTO category (category_id, parent_category_id, name)
VALUES (nextval('category_seq'), -1, 'Medical Reports');

INSERT INTO category (category_id, parent_category_id, name)
VALUES (nextval('category_seq'), -1, 'Contact Forms');

INSERT INTO _user (id, name, email, password, picture, is_enabled)
VALUES (nextval('_user_seq'), 'Admin', 'admin@email.com',
        '$2y$10$hkvMZ8EQBrOcNhwEjbEVCuex9L2u4UeSyE5aotM/9S/l0TYAu7HeK',
        'picture url',
        TRUE);
INSERT INTO user_roles (user_id, role)
VALUES (1, 'ADMINISTRATOR');

INSERT INTO _user (id, name, email, password, picture, is_enabled)
VALUES (nextval('_user_seq'), 'Duygu', 'duygu.celik@emu.edu.tr',
        '$2a$10$.iDveRrBWZh1dZsoBrguzOxQ0NhfvVJ1cjr7KHcNLuzSycVKbzgii',
        'picture url',
        TRUE);

INSERT INTO _user (id, name, email, password, picture, is_enabled)
VALUES (nextval('_user_seq'), 'Alexander', 'alexander@emu.edu.tr',
        '$2y$10$i44NP/3q3eitSIjqJjgdnemLoGcnhxmPU1Em3ZHr.DN6WD3PM3V66',
        'picture url',
        TRUE);

INSERT INTO _user (id, name, email, password, picture, is_enabled)
VALUES (nextval('_user_seq'), 'Ahmad', 'ahmad@email.com',
        '$2a$10$2O8NtpqXVLdESlILln1pWeuwAedflBgaNPSQ8zA4yWIfDZAXZn.um', 'picture url', TRUE);

INSERT INTO _user (id, name, email, password, picture, is_enabled)
VALUES (nextval('_user_seq'), 'Hussein', 'hussein@email.com',
        '$2a$10$dhADRWo0dnf64Ip4mSto3e3CdzHvMjJXHOpnxgdUOOKJ6KaFQAcyu', 'picture url', TRUE);
INSERT INTO _user (id, name, email, password, picture, is_enabled)
VALUES (nextval('_user_seq'), 'Rama', 'rama@email.com',
        '$2y$10$cUo8URaI8M2Risv36FuW9ODF.QUdYCyzEuWWC.Xkkh8l3XhxBkwNi',
        'picture url',
        TRUE);

INSERT INTO advisor (id, user_id, created_at)
VALUES ('AP24000001', 2, NOW());
INSERT INTO user_roles (user_id, role)
VALUES (2, 'ADVISOR');

INSERT INTO advisor (id, user_id, created_at)
VALUES ('AP24000002', 3, NOW());
INSERT INTO user_roles (user_id, role)
VALUES (3, 'ADVISOR');

INSERT INTO student (id, department, year, user_id, advisor_id, created_at)
VALUES ('24000001', 'CMSE', 4, 4, 'AP24000001', NOW());
INSERT INTO user_roles (user_id, role)
VALUES (4, 'STUDENT');

INSERT INTO student (id, department, year, user_id, advisor_id, created_at)
VALUES ('24000002', 'CMSE', 4, 5, 'AP24000001', NOW());
INSERT INTO user_roles (user_id, role)
VALUES (5, 'STUDENT');

INSERT INTO student (id, department, year, user_id, advisor_id, created_at)
VALUES ('24000003', 'CMSE', 4, 6, 'AP24000002', NOW());
INSERT INTO user_roles (user_id, role)
VALUES (6, 'STUDENT');

INSERT INTO category_permission (id, role, category_parent_category_id, category_category_id)
VALUES (nextval('category_permission_seq'), 'ADVISOR', -1, 4);
INSERT INTO category_permission (id, role, category_parent_category_id, category_category_id)
VALUES (nextval('category_permission_seq'), 'ADVISOR', -1, 5);
INSERT INTO category_permission (id, role, category_parent_category_id, category_category_id)
VALUES (nextval('category_permission_seq'), 'STUDENT', -1, 5);