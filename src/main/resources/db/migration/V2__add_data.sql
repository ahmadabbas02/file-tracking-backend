-- Initial Main Categories
INSERT INTO category (category_id, parent_category_id, name)
VALUES (nextval('category_seq'), -1, 'Medical Report'), -- id 1
       (nextval('category_seq'), -1, 'Contact Form'),   -- id 2
       (nextval('category_seq'), -1, 'Internship'),     -- id 3
       (nextval('category_seq'), -1, 'Petition');       -- id 4

-- TODO: TEST CATEGORIES TO BE REMOVED LATER
INSERT INTO category (category_id, parent_category_id, name)
VALUES (nextval('category_seq'), -1, 'Main Cat. 1'), -- id 5
       (nextval('category_seq'), 5, 'Sub Cat. 1'),   -- id 6
       (nextval('category_seq'), 5, 'Sub Cat. 2');   -- id 7

INSERT INTO _user (id, first_name, last_name, email, password, picture, is_enabled, phone_number)
VALUES (nextval('_user_seq'), 'Admin', 'Admin', 'admin@email.com',
        '$2y$10$hkvMZ8EQBrOcNhwEjbEVCuex9L2u4UeSyE5aotM/9S/l0TYAu7HeK', 'picture url', TRUE,
        '+90 533 123 4567'), -- id 1 admin
       (nextval('_user_seq'), 'Duygu', 'Çelik', 'duygu.celik@emu.edu.tr',
        '$2a$10$.iDveRrBWZh1dZsoBrguzOxQ0NhfvVJ1cjr7KHcNLuzSycVKbzgii', 'picture url', TRUE,
        '+90 533 123 4417'), -- id 2 duygu
       (nextval('_user_seq'), 'Alexander', 'Chefranov', 'alexander@emu.edu.tr',
        '$2y$10$i44NP/3q3eitSIjqJjgdnemLoGcnhxmPU1Em3ZHr.DN6WD3PM3V66', 'picture url', TRUE,
        '+90 533 123 4465'), -- id 3 alexander
       (nextval('_user_seq'), 'Hilal', 'Özbilgen', 'hilal.ozbilgen@emu.edu.tr',
        '$2y$10$3FeSckArqHEZhnDHZV5FvOvBc.ZEC/W6/qzm8lDVAZNzyfQfA.95K', 'picture url', TRUE,
        '+90 533 123 8456'), -- id 4 secretary
       (nextval('_user_seq'), 'Ahmad', 'Abbas', 'ahmad@email.com',
        '$2a$10$2O8NtpqXVLdESlILln1pWeuwAedflBgaNPSQ8zA4yWIfDZAXZn.um', 'picture url', TRUE,
        '+90 533 123 8464'), -- id 5 ahmad
       (nextval('_user_seq'), 'Hussein', 'Barada', 'hussein@email.com',
        '$2a$10$dhADRWo0dnf64Ip4mSto3e3CdzHvMjJXHOpnxgdUOOKJ6KaFQAcyu', 'picture url', TRUE,
        '+90 533 123 7854'), -- id 6 hussein
       (nextval('_user_seq'), 'Rama', 'Ayache', 'rama@email.com',
        '$2y$10$cUo8URaI8M2Risv36FuW9ODF.QUdYCyzEuWWC.Xkkh8l3XhxBkwNi', 'picture url', TRUE,
        '+90 533 123 1645'), -- id 7 rama
       (nextval('_user_seq'), 'Zeki', 'Bayram', 'zeki.bayram@emu.edu.tr',
        '$2y$10$LZHGRKf./k89lLM1lD2vOeV1rrQsG5BWKiFbLXHrYgTmQLeLvtRyq', 'picture url', TRUE,
        '+90 533 854 4158'); -- id 8 zeki

INSERT INTO user_roles (user_id, role)
VALUES (1, 'ADMINISTRATOR'),
       (2, 'ADVISOR'),
       (3, 'ADVISOR'),
       (4, 'SECRETARY'),
       (5, 'STUDENT'),
       (6, 'STUDENT'),
       (7, 'STUDENT'),
       (8, 'CHAIR');

INSERT INTO advisor (id, user_id, created_at)
VALUES ('AP24000001', 2, NOW()),
       ('AP24000002', 3, NOW());

INSERT INTO student (id, program, year, user_id, advisor_id, created_at, internship_completion_status, payment_status)
VALUES ('24000001', 'CMSE', 4, 5, 'AP24000001', NOW(), 'INCOMPLETE', 'NOT_PAID'),
       ('24000002', 'CMSE', 4, 6, 'AP24000001', NOW(), 'INCOMPLETE', 'NOT_PAID'),
       ('24000003', 'CMSE', 4, 7, 'AP24000002', NOW(), 'INCOMPLETE', 'NOT_PAID');

-- Chair categories
INSERT INTO category_permission (id, role, category_parent_category_id, category_category_id)
VALUES (nextval('category_permission_seq'), 'CHAIR', -1, 1), -- Medical Report
       (nextval('category_permission_seq'), 'CHAIR', -1, 2), -- Contact Form
       (nextval('category_permission_seq'), 'CHAIR', -1, 3), -- Internship
       (nextval('category_permission_seq'), 'CHAIR', -1, 4); -- Petition

-- Secretary categories
-- Chair categories
INSERT INTO category_permission (id, role, category_parent_category_id, category_category_id)
VALUES (nextval('category_permission_seq'), 'SECRETARY', -1, 1), -- Medical Report
       (nextval('category_permission_seq'), 'SECRETARY', -1, 2), -- Contact Form
       (nextval('category_permission_seq'), 'SECRETARY', -1, 3), -- Internship
       (nextval('category_permission_seq'), 'SECRETARY', -1, 4); -- Petition

-- Advisor categories
-- Medical Report
INSERT INTO category_permission (id, role, category_parent_category_id, category_category_id)
VALUES (nextval('category_permission_seq'), 'ADVISOR', -1, 1), -- Medical Report
       (nextval('category_permission_seq'), 'ADVISOR', -1, 2), -- Contact Form
       (nextval('category_permission_seq'), 'ADVISOR', -1, 3), -- Internship
       (nextval('category_permission_seq'), 'ADVISOR', -1, 4); -- Petition

-- Student categories
INSERT INTO category_permission (id, role, category_parent_category_id, category_category_id)
VALUES (nextval('category_permission_seq'), 'STUDENT', -1, 1), -- Medical Report
       (nextval('category_permission_seq'), 'STUDENT', -1, 2), -- Contact Form
       (nextval('category_permission_seq'), 'STUDENT', -1, 4); -- Petition