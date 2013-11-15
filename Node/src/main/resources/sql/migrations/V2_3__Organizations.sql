DROP TABLE IF EXISTS `organization`;

CREATE TABLE `organization` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;

INSERT INTO `organization` (`id`,`name`) VALUES (1,'Default Organization');


ALTER TABLE `datastock_root` ADD `owner_organization` BIGINT( 20 ) NOT NULL , ADD INDEX ( `owner_organization` );
UPDATE `datastock_root` SET `owner_organization`=1;


ALTER TABLE `usergroup` ADD `organization` BIGINT( 20 ) NULL , ADD INDEX ( `organization` );
UPDATE `usergroup` SET `organization`=1;


ALTER TABLE `user` ADD `organization` BIGINT( 20 ) NULL , ADD INDEX ( `organization` );
UPDATE `user` SET `organization`=1;

INSERT INTO `usergroup_roles` (`UserGroup_ID`,`ROLES`) VALUES ( 102, 'SUPER_ADMIN_ROLE');

ALTER TABLE `organization` ADD `STREETADDRESS` varchar(255) DEFAULT NULL, ADD `ZIPCODE` varchar(255) DEFAULT NULL, ADD `COUNTRY` varchar(255) DEFAULT NULL, ADD `CITY` varchar(255) DEFAULT NULL;
ALTER TABLE `organization` ADD `ORGANISATIONUNIT` varchar(255) DEFAULT NULL, ADD `SECTOR_ID` bigint(20) DEFAULT NULL, ADD INDEX (`SECTOR_ID`);

ALTER TABLE `user` CHANGE `organization` `organization` BIGINT( 20 ) NULL;
ALTER TABLE `user` DROP INDEX `FK_USER_SECTOR_ID`, DROP `SECTOR_ID`, DROP `ORGANISATION`, DROP `ORGANISATIONUNIT`;
