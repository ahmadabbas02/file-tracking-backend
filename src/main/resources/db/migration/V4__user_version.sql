ALTER TABLE _user
    ADD version INTEGER NOT NULL default 0;

ALTER TABLE advisor
    ADD version INTEGER NOT NULL default 0;

ALTER TABLE student
    ADD version INTEGER NOT NULL default 0;