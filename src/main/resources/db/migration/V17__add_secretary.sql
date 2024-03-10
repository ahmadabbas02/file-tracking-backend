INSERT INTO _user (id, name, email, password, picture, phone_number ,is_enabled)
VALUES (nextval('_user_seq'), 'Hilal Özbilgen', 'hilal.ozbilgen@emu.edu.tr',
        '$2y$10$3FeSckArqHEZhnDHZV5FvOvBc.ZEC/W6/qzm8lDVAZNzyfQfA.95K',
        'picture url',
        '',
        TRUE);
INSERT INTO user_roles (user_id, role)
VALUES (7, 'SECRETARY');