ALTER TABLE document
    ADD subject VARCHAR(255);
INSERT INTO category (category_id, parent_category_id, name)
VALUES (nextval('category_seq'), -1, 'Petition');