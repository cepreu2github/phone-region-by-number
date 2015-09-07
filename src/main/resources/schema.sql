CREATE SCHEMA IF NOT EXISTS `phones` DEFAULT CHARACTER SET utf8;
USE phones;
CREATE TABLE IF NOT EXISTS `config` (`id` INT NOT NULL PRIMARY KEY, `lastupdated` DATETIME, `ABC-3_key` VARCHAR(50),
    `ABC-4_key` VARCHAR(50), `ABC-8_key` VARCHAR(50), `DEF-9_key` VARCHAR(50));
CREATE TABLE IF NOT EXISTS `numbers` (`prefix` INT NOT NULL, `start` INT NOT NULL, `end` INT NOT NULL,
    `region` VARCHAR(250) NOT NULL, PRIMARY KEY (`prefix`, `start`, `end`));