-- --------------------
-- stock access rights
-- --------------------

CREATE TABLE `stock_access_right` (
  `stock_id` bigint(20) NOT NULL,
  `access_right_type` varchar(10) NOT NULL,
  `ug_id` bigint(20) NOT NULL,
  `value` smallint(11) NOT NULL,
  PRIMARY KEY (`stock_id`,`access_right_type`,`ug_id`),
  KEY `ug_id` (`ug_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

ALTER TABLE `organization` ADD `admin_user` BIGINT( 20 ) NULL , ADD `admin_group` BIGINT( 20 ) NULL;
ALTER TABLE `organization` ADD INDEX ( `admin_user` );
ALTER TABLE `organization` ADD INDEX ( `admin_group` );

-- ---------------------------------------------
-- Non root data stocks / single table strategy
-- ---------------------------------------------

DROP TABLE IF EXISTS `datastock`;
CREATE TABLE IF NOT EXISTS `datastock` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `datastock_type` varchar(3) NOT NULL,
  `description` text,
  `title` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `owner_organization` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `name` (`name`),
  KEY `owner_organization` (`owner_organization`),
  KEY `datastock_type` (`datastock_type`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 ;


DROP TABLE IF EXISTS `datastock_titles`;
CREATE TABLE `datastock_titles` (
  `datastock_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`datastock_id`,`lStringMap_ID`),
  KEY `FK_datastock_titles_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `datastock_descriptions`;
CREATE TABLE `datastock_descriptions` (
  `datastock_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`datastock_id`,`lStringMap_ID`),
  KEY `FK_datastock_descriptions_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


-- Copy data from RDS
INSERT INTO `datastock` (SELECT r.`ID`, 'rds' as `datastock_type`, r.`description`, r.`title`, r.`name`, r.`owner_organization` FROM `datastock_root` r);
INSERT INTO `datastock_titles` (SELECT * FROM `datastock_root_titles`);
INSERT INTO `datastock_descriptions` (SELECT * FROM `datastock_root_descriptions`);

-- Remove old tables
DROP TABLE `datastock_root`;
DROP TABLE `datastock_root_titles`;
DROP TABLE `datastock_root_descriptions`;

-- init anonymous user's rights: 9 means READ and EXPORT (full dataset view)
INSERT INTO `stock_access_right` SELECT `ID` as `stock_id`, 'User' as `access_right_type`, 0 as 'ug_id', 0 as `value` FROM `datastock`;
UPDATE `stock_access_right` SET `value`=9 WHERE `access_right_type`='User' AND `stock_id`=1 AND `ug_id`=0 LIMIT 1;