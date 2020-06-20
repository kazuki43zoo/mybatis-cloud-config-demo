DELETE FROM settings;
INSERT INTO settings (key, value) VALUES('name','current database');
INSERT INTO settings (key, value) VALUES('timeout','1000');
commit;