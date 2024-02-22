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

-- Admin:admin
INSERT INTO _user (id, name, email, password, is_enabled)
VALUES (nextval('_user_seq'), 'Admin', 'admin@email.com',
        '$2y$10$hkvMZ8EQBrOcNhwEjbEVCuex9L2u4UeSyE5aotM/9S/l0TYAu7HeK', TRUE);
INSERT INTO user_roles (user_id, role)
VALUES (1, 'ADMINISTRATOR');

INSERT INTO _user (id, name, email, password, is_enabled)
VALUES (nextval('_user_seq'), 'Duygu Celik', 'duygu.celik@emu.edu.tr',
        '$2a$10$.iDveRrBWZh1dZsoBrguzOxQ0NhfvVJ1cjr7KHcNLuzSycVKbzgii',
        TRUE);

INSERT INTO _user (id, name, email, password, is_enabled)
VALUES (nextval('_user_seq'), 'Ahmad', 'ahmad@email.com',
        '$2a$10$2O8NtpqXVLdESlILln1pWeuwAedflBgaNPSQ8zA4yWIfDZAXZn.um', TRUE);

INSERT INTO _user (id, name, email, password, is_enabled)
VALUES (nextval('_user_seq'), 'Hussein', 'hussein@email.com',
        '$2a$10$dhADRWo0dnf64Ip4mSto3e3CdzHvMjJXHOpnxgdUOOKJ6KaFQAcyu', TRUE);

INSERT INTO advisor (id, user_id, created_at)
VALUES ('AP24000001', 2, NOW());
INSERT INTO user_roles (user_id, role)
VALUES (2, 'ADVISOR');

INSERT INTO student (id, department, year, picture, user_id, advisor_id, created_at)
VALUES ('24000001', 'CMSE', 4, 'picture url', 3, 'AP24000001', NOW());
INSERT INTO user_roles (user_id, role)
VALUES (3, 'STUDENT');

INSERT INTO student (id, department, year, picture, user_id, advisor_id, created_at)
VALUES ('24000002', 'CMSE', 4, 'picture url', 4, 'AP24000001', NOW());
INSERT INTO user_roles (user_id, role)
VALUES (4, 'STUDENT');

INSERT INTO category_permission (id, role, category_parent_category_id, category_category_id)
VALUES (nextval('category_permission_seq'), 'ADVISOR', -1, 4);
INSERT INTO category_permission (id, role, category_parent_category_id, category_category_id)
VALUES (nextval('category_permission_seq'), 'ADVISOR', -1, 5);