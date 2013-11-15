DROP TABLE IF EXISTS `datastock_names`;

CREATE TABLE IF NOT EXISTS `datastock_lciamethod` (
	`DataStock_ID` bigint(20) NOT NULL,
	`lciaMethods_ID` bigint(20) NOT NULL,
	PRIMARY KEY (`DataStock_ID`,`lciaMethods_ID`),
	KEY `FK_DATASTOCK_LCIAMETHOD_lciaMethods_ID` (`lciaMethods_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `datastock_root`;
CREATE TABLE `datastock_root` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` text,
  `title` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

DROP TABLE IF EXISTS `datastock_root_titles`;
CREATE TABLE `datastock_root_titles` (
  `datastock_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`datastock_id`,`lStringMap_ID`),
  KEY `FK_datastock_root_titles_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `datastock_root_descriptions`;
CREATE TABLE `datastock_root_descriptions` (
  `datastock_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`datastock_id`,`lStringMap_ID`),
  KEY `FK_datastock_root_descriptions_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


INSERT INTO `datastock_root` (`ID`, `description`, `title`, `name`) VALUES (NULL, 'Default Root Data Stock', 'Default Root Data Stock', 'default');

ALTER TABLE `datastock_root` ADD UNIQUE `name` ( `name` );

ALTER TABLE `contact` ADD `root_stock_id` BIGINT( 20 ) NOT NULL;
ALTER TABLE `flow` ADD `root_stock_id` BIGINT( 20 ) NOT NULL;
ALTER TABLE `flowproperty` ADD `root_stock_id` BIGINT( 20 ) NOT NULL;
ALTER TABLE `lciamethod` ADD `root_stock_id` BIGINT( 20 ) NOT NULL;
ALTER TABLE `process` ADD `root_stock_id` BIGINT( 20 ) NOT NULL;
ALTER TABLE `source` ADD `root_stock_id` BIGINT( 20 ) NOT NULL;
ALTER TABLE `unitgroup` ADD `root_stock_id` BIGINT( 20 ) NOT NULL;

ALTER TABLE `contact` ADD INDEX ( `root_stock_id` );
ALTER TABLE `flow` ADD INDEX ( `root_stock_id` );
ALTER TABLE `flowproperty` ADD INDEX ( `root_stock_id` );
ALTER TABLE `lciamethod` ADD INDEX ( `root_stock_id` );
ALTER TABLE `process` ADD INDEX ( `root_stock_id` );
ALTER TABLE `source` ADD INDEX ( `root_stock_id` );
ALTER TABLE `unitgroup` ADD INDEX ( `root_stock_id` );

UPDATE `contact` SET `root_stock_id`=1;
UPDATE `flow` SET `root_stock_id`=1;
UPDATE `flowproperty` SET `root_stock_id`=1;
UPDATE `lciamethod` SET `root_stock_id`=1;
UPDATE `process` SET `root_stock_id`=1;
UPDATE `source` SET `root_stock_id`=1;
UPDATE `unitgroup` SET `root_stock_id`=1;

DROP TABLE IF EXISTS `configuration`;
CREATE TABLE `configuration` (
  `default_datastock_name` varchar(255) NOT NULL,
  `default_datastock_is_root` tinyint(1)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

INSERT INTO `configuration` (`default_datastock_name`,`default_datastock_is_root`) VALUES('default',1);