ALTER TABLE lciamethod DROP FOREIGN KEY `FK_LCIAMETHOD_TIMEINFORMATION_ID` ;
ALTER TABLE lciamethod DROP COLUMN `TIMEINFORMATION_ID`, DROP INDEX `FK_LCIAMETHOD_TIMEINFORMATION_ID` ;

SET foreign_key_checks = 0;
DROP TABLE lciamethod_timeinformation;
SET foreign_key_checks = 1;

DROP TABLE IF EXISTS `lciamethod_ti_durationdescription`;
CREATE TABLE IF NOT EXISTS `lciamethod_ti_durationdescription` (
  `lciamethod_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_lciamethod_ti_durationdescription_lciamethod_id` (`lciamethod_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `lciamethod_ti_referenceyeardescription`;
CREATE TABLE IF NOT EXISTS `lciamethod_ti_referenceyeardescription` (
  `lciamethod_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `lciamethodti_referenceyeardescriptionlciamethod_id` (`lciamethod_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

