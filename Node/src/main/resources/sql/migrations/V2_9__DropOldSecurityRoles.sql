-- --------------------
-- drop all old security roles
-- --------------------

ALTER TABLE `usergroup` ADD `super_admin_permission` TINYINT( 1 ) NOT NULL;
ALTER TABLE `usergroup` ADD INDEX ( `super_admin_permission` );

UPDATE `usergroup` SET `super_admin_permission`=1 WHERE `ID` IN (SELECT `UserGroup_ID` as `ID` FROM `usergroup_roles` WHERE `ROLES` = 'SUPER_ADMIN_ROLE' );
DROP TABLE `usergroup_roles`;

-- never offered on UI, no migration
DROP TABLE `user_roles`;