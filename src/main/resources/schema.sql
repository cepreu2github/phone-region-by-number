CREATE SCHEMA IF NOT EXISTS `phones` DEFAULT CHARACTER SET utf8;
USE phones;
CREATE TABLE IF NOT EXISTS `config` (`id` INT NOT NULL PRIMARY KEY, `lastupdated` DATETIME, `ABC-3_key` CHAR(32),
    `ABC-4_key` CHAR(32), `ABC-8_key` CHAR(32), `DEF-9_key` CHAR(32));
CREATE TABLE IF NOT EXISTS `numbers` (`min` BIGINT, `max` BIGINT, `region` VARCHAR(250),
    PRIMARY KEY (`min`, `max`));