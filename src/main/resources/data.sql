USE phones;
INSERT INTO config (`id`, `lastupdated`, `ABC-3_key`, `ABC-4_key`, `ABC-8_key`, `DEF-9_key`)
VALUES(1, NULL, NULL, NULL, NULL, NULL) ON DUPLICATE KEY UPDATE id=1;