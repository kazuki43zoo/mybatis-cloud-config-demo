DELETE FROM settings;
INSERT INTO settings (key, value) VALUES('name','v1 database');
INSERT INTO settings (key, value) VALUES('timeout','2000');
commit;