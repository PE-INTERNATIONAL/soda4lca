--
-- Adaptions for cache columns
--

ALTER TABLE `contact` ADD `name_cache` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL , ADD `classification_cache` VARCHAR( 100 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE `contact` ADD INDEX ( `name_cache` );
ALTER TABLE `contact` ADD INDEX ( `classification_cache` );

ALTER TABLE `flow` ADD `name_cache` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL , ADD `classification_cache` VARCHAR( 100 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL , ADD `referenceProperty_cache` VARCHAR( 20 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL , ADD `referencePropertyUnit_cache` VARCHAR( 10 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE `flow` ADD INDEX ( `name_cache` );
ALTER TABLE `flow` ADD INDEX ( `classification_cache` );
ALTER TABLE `flow` ADD INDEX ( `referenceProperty_cache` );
ALTER TABLE `flow` ADD INDEX ( `referencePropertyUnit_cache` );

ALTER TABLE `flowproperty` ADD `name_cache` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL , ADD `classification_cache` VARCHAR( 100 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL , ADD `defaultUnitGroup_cache` VARCHAR( 20 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL , ADD `defaultUnit_cache` VARCHAR( 10 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE `flowproperty` ADD INDEX ( `name_cache` );
ALTER TABLE `flowproperty` ADD INDEX ( `classification_cache` );
ALTER TABLE `flowproperty` ADD INDEX ( `defaultUnitGroup_cache` );
ALTER TABLE `flowproperty` ADD INDEX ( `defaultUnit_cache` );

ALTER TABLE `lciamethod` ADD `name_cache` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL , ADD `classification_cache` VARCHAR( 100 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE `lciamethod` ADD INDEX ( `name_cache` );
ALTER TABLE `lciamethod` ADD INDEX ( `classification_cache` );

ALTER TABLE `process` ADD `name_cache` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL , ADD `classification_cache` VARCHAR( 100 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL , ADD `lciMethodInformation_cache` VARCHAR( 20 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL , ADD `complianceSystem_cache` VARCHAR( 1 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE `process` ADD INDEX ( `name_cache` );
ALTER TABLE `process` ADD INDEX ( `classification_cache` );
ALTER TABLE `process` ADD INDEX ( `lciMethodInformation_cache` );
ALTER TABLE `process` ADD INDEX ( `complianceSystem_cache` );

ALTER TABLE `source` ADD `name_cache` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL , ADD `classification_cache` VARCHAR( 100 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE `source` ADD INDEX ( `name_cache` );
ALTER TABLE `source` ADD INDEX ( `classification_cache` );

ALTER TABLE `unitgroup` ADD `name_cache` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL , ADD `classification_cache` VARCHAR( 100 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL , ADD `referenceUnit_cache` VARCHAR( 10 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE `unitgroup` ADD INDEX ( `name_cache` );
ALTER TABLE `unitgroup` ADD INDEX ( `classification_cache` );
ALTER TABLE `unitgroup` ADD INDEX ( `referenceUnit_cache` );