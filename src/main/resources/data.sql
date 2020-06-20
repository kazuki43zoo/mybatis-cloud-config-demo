DELETE FROM settings;
INSERT INTO settings (key, value) VALUES('name','default database');
INSERT INTO settings (key, value) VALUES('timeout','0');
commit;