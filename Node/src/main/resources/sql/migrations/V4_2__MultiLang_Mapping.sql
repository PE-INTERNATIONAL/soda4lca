RENAME TABLE `process_allocations` TO `process_lcimethodapproaches`;

DROP TABLE IF EXISTS `dataset_descriptions`;
DROP TABLE IF EXISTS `dataset_names`;

DROP TABLE IF EXISTS `lstring`;
DROP TABLE IF EXISTS `ltext`;


ALTER TABLE `stock_access_right` DROP PRIMARY KEY;
ALTER TABLE `stock_access_right` ADD `ID` bigint(20) NOT NULL AUTO_INCREMENT KEY;
ALTER TABLE `stock_access_right` ADD UNIQUE KEY `UNQ_stock_access_right_0` (`stock_id`,`access_right_type`,`ug_id`);

UPDATE `stock_access_right` SET ID=1 WHERE stock_id=1;


/* contact - description */
ALTER TABLE contact DROP description;

DROP TABLE IF EXISTS `contact_description`;

CREATE TABLE `contact_description` (
  `Contact_ID` bigint(20) DEFAULT NULL,
  `value` TEXT DEFAULT NULL,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_Contact_DESCRIPTION_Contact_ID` (`Contact_ID`),
  CONSTRAINT `FK_Contact_DESCRIPTION_Contact_ID` FOREIGN KEY (`Contact_ID`) REFERENCES `contact` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/* contact - name */
ALTER TABLE contact DROP name;

DROP TABLE IF EXISTS `contact_name`;

CREATE TABLE `contact_name` (
  `contact_id` bigint(20) DEFAULT NULL,
  `value` TEXT DEFAULT NULL,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_Contact_NAME_contact_id` (`contact_id`),
  CONSTRAINT `FK_Contact_NAME_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `contact` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/* contact - shortname */
ALTER TABLE contact DROP shortName;

DROP TABLE IF EXISTS `contact_shortname`;

CREATE TABLE `contact_shortname` (
  `contact_id` bigint(20) DEFAULT NULL,
  `value` TEXT DEFAULT NULL,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_contact_shortName_contact_id` (`contact_id`),
  CONSTRAINT `FK_contact_shortName_contact_id` FOREIGN KEY (`contact_id`) REFERENCES `contact` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



/* datastock */
ALTER TABLE datastock DROP description;
DROP TABLE IF EXISTS `datastock_descriptions`;


DROP TABLE IF EXISTS `datastock_description`;

CREATE TABLE `datastock_description` (
  `datastock_id` bigint(20) DEFAULT NULL,
  `value` TEXT DEFAULT NULL,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_datastock_description_datastock_id` (`datastock_id`),
  CONSTRAINT `FK_datastock_description_datastock_id` FOREIGN KEY (`datastock_id`) REFERENCES `datastock` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



ALTER TABLE datastock DROP title;
DROP TABLE IF EXISTS `datastock_titles`;

DROP TABLE IF EXISTS `datastock_longtitle`;

CREATE TABLE `datastock_longtitle` (
  `datastock_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_datastock_longTitle_datastock_id` (`datastock_id`),
  CONSTRAINT `FK_datastock_longTitle_datastock_id` FOREIGN KEY (`datastock_id`) REFERENCES `datastock` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO datastock_longtitle VALUES (1, 'Default root data stock', 'en');
INSERT INTO datastock_description VALUES (1, 'This is the default root data stock', 'en');


/* exchange */
ALTER TABLE exchange DROP comment;

DROP TABLE IF EXISTS `exchange_comment`;

CREATE TABLE `exchange_comment` (
  `exchange_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_exchange_comment_exchange_id` (`exchange_id`),
  CONSTRAINT `FK_exchange_comment_exchange_id` FOREIGN KEY (`exchange_id`) REFERENCES `exchange` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



/* flow */
ALTER TABLE flow DROP description;

DROP TABLE IF EXISTS `flow_description`;

CREATE TABLE `flow_description` (
  `flow_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_Flow_DESCRIPTION_flow_id` (`flow_id`),
  CONSTRAINT `FK_Flow_DESCRIPTION_flow_id` FOREIGN KEY (`flow_id`) REFERENCES `flow` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE flow DROP name;

DROP TABLE IF EXISTS `flow_name`;

CREATE TABLE `flow_name` (
  `flow_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_Flow_NAME_flow_id` (`flow_id`),
  CONSTRAINT `FK_Flow_NAME_flow_id` FOREIGN KEY (`flow_id`) REFERENCES `flow` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE flow DROP synonyms;

DROP TABLE IF EXISTS `flow_synonyms`;

DROP TABLE IF EXISTS `flow_synonyms`;
CREATE TABLE `flow_synonyms` (
  `flow_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_flow_synonyms_flow_id` (`flow_id`),
  CONSTRAINT `FK_flow_synonyms_flow_id` FOREIGN KEY (`flow_id`) REFERENCES `flow` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/* flowproperty */
ALTER TABLE flowproperty DROP description;

DROP TABLE IF EXISTS `flowproperty_description`;
CREATE TABLE `flowproperty_description` (
  `flowproperty_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_FlowProperty_DESCRIPTION_flowproperty_id` (`flowproperty_id`),
  CONSTRAINT `FK_FlowProperty_DESCRIPTION_flowproperty_id` FOREIGN KEY (`flowproperty_id`) REFERENCES `flowproperty` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE flowproperty DROP name;

DROP TABLE IF EXISTS `flowproperty_name`;

CREATE TABLE `flowproperty_name` (
  `flowproperty_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_FlowProperty_NAME_flowproperty_id` (`flowproperty_id`),
  CONSTRAINT `FK_FlowProperty_NAME_flowproperty_id` FOREIGN KEY (`flowproperty_id`) REFERENCES `flowproperty` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE flowproperty DROP synonyms;

DROP TABLE IF EXISTS `flowproperty_synonyms`;

CREATE TABLE `flowproperty_synonyms` (
  `flowproperty_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_flowproperty_synonyms_flowproperty_id` (`flowproperty_id`),
  CONSTRAINT `FK_flowproperty_synonyms_flowproperty_id` FOREIGN KEY (`flowproperty_id`) REFERENCES `flowproperty` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/* flowpropertydescription */
ALTER TABLE flowpropertydescription DROP description;

DROP TABLE IF EXISTS `flowpropdesc_descriptions`;

DROP TABLE IF EXISTS `flowpropertydescription_description`;

CREATE TABLE `flowpropertydescription_description` (
  `flowpropertydescription_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `flwprprtydscrptiondescriptionflwprprtydscriptionid` (`flowpropertydescription_id`),
  CONSTRAINT `flwprprtydscrptiondescriptionflwprprtydscriptionid` FOREIGN KEY (`flowpropertydescription_id`) REFERENCES `flowpropertydescription` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



/* lciamethod */
DROP TABLE IF EXISTS `lciamethod_timeinformation`;

CREATE TABLE `lciamethod_timeinformation` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE lciamethod ADD `TIMEINFORMATION_ID` bigint(20) DEFAULT NULL;
ALTER TABLE lciamethod ADD KEY `FK_LCIAMETHOD_TIMEINFORMATION_ID` (`TIMEINFORMATION_ID`);
ALTER TABLE lciamethod ADD CONSTRAINT `FK_LCIAMETHOD_TIMEINFORMATION_ID` FOREIGN KEY (`TIMEINFORMATION_ID`) REFERENCES `lciamethod_timeinformation` (`ID`);



ALTER TABLE lciamethod DROP description;

DROP TABLE IF EXISTS `lciamethod_shortDescription`;

DROP TABLE IF EXISTS `lciamethod_description`;

CREATE TABLE `lciamethod_description` (
  `lciamethod_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_LCIAMethod_DESCRIPTION_lciamethod_id` (`lciamethod_id`),
  CONSTRAINT `FK_LCIAMethod_DESCRIPTION_lciamethod_id` FOREIGN KEY (`lciamethod_id`) REFERENCES `lciamethod` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



ALTER TABLE lciamethod DROP durationDescription;

DROP TABLE IF EXISTS `lciamethod_durationdescription`;

CREATE TABLE `lciamethod_durationdescription` (
  `lciamethod_timeinformation_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `lcmthoddurationDescriptionlcmthodtimeinformationid` (`lciamethod_timeinformation_id`),
  CONSTRAINT `lcmthoddurationDescriptionlcmthodtimeinformationid` FOREIGN KEY (`lciamethod_timeinformation_id`) REFERENCES `lciamethod_timeinformation` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



ALTER TABLE lciamethod DROP name;

DROP TABLE IF EXISTS `lciamethod_name`;

CREATE TABLE `lciamethod_name` (
  `lciamethod_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_LCIAMethod_NAME_lciamethod_id` (`lciamethod_id`),
  CONSTRAINT `FK_LCIAMethod_NAME_lciamethod_id` FOREIGN KEY (`lciamethod_id`) REFERENCES `lciamethod` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



ALTER TABLE lciamethod DROP referenceYearDescription;

DROP TABLE IF EXISTS `lciamethod_referenceyeardescription`;

CREATE TABLE `lciamethod_referenceyeardescription` (
  `lciamethod_timeinformation_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `lcmthdrfrenceYearDescriptionlcmthdtmeinformationid` (`lciamethod_timeinformation_id`),
  CONSTRAINT `lcmthdrfrenceYearDescriptionlcmthdtmeinformationid` FOREIGN KEY (`lciamethod_timeinformation_id`) REFERENCES `lciamethod_timeinformation` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



ALTER TABLE lciamethodcharacterisationfactor DROP shortDescription;

DROP TABLE IF EXISTS lciamethod_charfac_shortdescription;

DROP TABLE IF EXISTS `lciamethodcharfactor_description`;

CREATE TABLE `lciamethodcharfactor_description` (
  `lciamethodcharacterisationfactor_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `lcmthdchrfctrdescriptionlcmthdchrctrsationfactorid` (`lciamethodcharacterisationfactor_id`),
  CONSTRAINT `lcmthdchrfctrdescriptionlcmthdchrctrsationfactorid` FOREIGN KEY (`lciamethodcharacterisationfactor_id`) REFERENCES `lciamethodcharacterisationfactor` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



/* process */
DROP TABLE IF EXISTS `process_accessinformation`;
CREATE TABLE `process_accessinformation` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `COPYRIGHT` tinyint(1) DEFAULT '0',
  `LICENSETYPE` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE process DROP COPYRIGHT;
ALTER TABLE process DROP LICENSETYPE;


DROP TABLE IF EXISTS `process_geography`;
CREATE TABLE `process_geography` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `LOCATION` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE process DROP LOCATION;


DROP TABLE IF EXISTS `process_timeinformation`;
CREATE TABLE `process_timeinformation` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `REFERENCEYEAR` int(11) DEFAULT NULL,
  `VALIDUNTIL` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE process DROP REFERENCEYEAR;
ALTER TABLE process DROP VALIDUNTIL;


DROP TABLE IF EXISTS `processname`;
CREATE TABLE `processname` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  FOO int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE process ADD `GEOGRAPHY_ID` bigint(20) DEFAULT NULL;
ALTER TABLE process ADD `PROCESSNAME_ID` bigint(20) DEFAULT NULL;
ALTER TABLE process ADD `TIMEINFORMATION_ID` bigint(20) DEFAULT NULL;
ALTER TABLE process ADD `ACCESSINFORMATION_ID` bigint(20) DEFAULT NULL;


ALTER TABLE process ADD KEY `FK_PROCESS_TIMEINFORMATION_ID` (`TIMEINFORMATION_ID`);
ALTER TABLE process ADD KEY `FK_PROCESS_PROCESSNAME_ID` (`PROCESSNAME_ID`);
ALTER TABLE process ADD KEY `FK_PROCESS_GEOGRAPHY_ID` (`GEOGRAPHY_ID`);
ALTER TABLE process ADD KEY `FK_PROCESS_ACCESSINFORMATION_ID` (`ACCESSINFORMATION_ID`);



ALTER TABLE lciaresult DROP comment;

DROP TABLE IF EXISTS `lciaresult_comment`;

CREATE TABLE `lciaresult_comment` (
  `lciaresult_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_lciaresult_comment_lciaresult_id` (`lciaresult_id`),
  CONSTRAINT `FK_lciaresult_comment_lciaresult_id` FOREIGN KEY (`lciaresult_id`) REFERENCES `lciaresult` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE process DROP description;

DROP TABLE IF EXISTS `process_description`;

CREATE TABLE `process_description` (
  `process_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_Process_DESCRIPTION_process_id` (`process_id`),
  CONSTRAINT `FK_Process_DESCRIPTION_process_id` FOREIGN KEY (`process_id`) REFERENCES `process` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



ALTER TABLE process DROP locationRestriction;

DROP TABLE IF EXISTS `process_locationrestriction`;

CREATE TABLE `process_locationrestriction` (
  `process_geography_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `process_locationrestriction_process_geography_id` (`process_geography_id`),
  CONSTRAINT `process_locationrestriction_process_geography_id` FOREIGN KEY (`process_geography_id`) REFERENCES `process_geography` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



ALTER TABLE process DROP name;

DROP TABLE IF EXISTS `process_name`;

CREATE TABLE `process_name` (
  `process_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_Process_NAME_process_id` (`process_id`),
  CONSTRAINT `FK_Process_NAME_process_id` FOREIGN KEY (`process_id`) REFERENCES `process` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




DROP TABLE IF EXISTS `process_otherreference`;

DROP TABLE IF EXISTS `process_quantref_otherreference`;

CREATE TABLE `process_quantref_otherreference` (
  `internalquantitativereference_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `prcssqntrfotherReferencentrnlqntitativereferenceid` (`internalquantitativereference_id`),
  CONSTRAINT `prcssqntrfotherReferencentrnlqntitativereferenceid` FOREIGN KEY (`internalquantitativereference_id`) REFERENCES `quantitativereference` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `process_referenceids`;

DROP TABLE IF EXISTS `process_quantref_referenceids`;

CREATE TABLE `process_quantref_referenceids` (
  `internalquantitativereference_id` bigint(20) DEFAULT NULL,
  `refId` int(11) DEFAULT NULL,
  KEY `prcssqntrefreferenceIdsntrnlqantitativereferenceid` (`internalquantitativereference_id`),
  CONSTRAINT `prcssqntrefreferenceIdsntrnlqantitativereferenceid` FOREIGN KEY (`internalquantitativereference_id`) REFERENCES `quantitativereference` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



ALTER TABLE process DROP synonyms;

DROP TABLE IF EXISTS `process_synonyms`;

CREATE TABLE `process_synonyms` (
  `process_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_process_synonyms_process_id` (`process_id`),
  CONSTRAINT `FK_process_synonyms_process_id` FOREIGN KEY (`process_id`) REFERENCES `process` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `process_purpose`;

DROP TABLE IF EXISTS `process_technicalpurpose`;

CREATE TABLE `process_technicalpurpose` (
  `process_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_process_technicalPurpose_process_id` (`process_id`),
  CONSTRAINT `FK_process_technicalPurpose_process_id` FOREIGN KEY (`process_id`) REFERENCES `process` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



ALTER TABLE process DROP timeDescription;

DROP TABLE IF EXISTS `process_timedescription`;

CREATE TABLE `process_timedescription` (
  `process_timeinformation_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `process_timeDescription_process_timeinformation_id` (`process_timeinformation_id`),
  CONSTRAINT `process_timeDescription_process_timeinformation_id` FOREIGN KEY (`process_timeinformation_id`) REFERENCES `process_timeinformation` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



ALTER TABLE process DROP useadvice;

DROP TABLE IF EXISTS `process_useadvice`;

CREATE TABLE `process_useadvice` (
  `process_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_process_useAdvice_process_id` (`process_id`),
  CONSTRAINT `FK_process_useAdvice_process_id` FOREIGN KEY (`process_id`) REFERENCES `process` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



ALTER TABLE process DROP useRestrictions;

DROP TABLE IF EXISTS `process_userestrictions`;

CREATE TABLE `process_userestrictions` (
  `process_accessinformation_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `processuseRestrictionsprocess_accessinformation_id` (`process_accessinformation_id`),
  CONSTRAINT `processuseRestrictionsprocess_accessinformation_id` FOREIGN KEY (`process_accessinformation_id`) REFERENCES `process_accessinformation` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



ALTER TABLE process DROP technicalPurpose;
ALTER TABLE process DROP PROCESSNAME_ID;

DROP TABLE IF EXISTS `processname`;


ALTER TABLE process DROP baseName;

DROP TABLE IF EXISTS `processname_base`;

CREATE TABLE `processname_base` (
  `process_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_processname_base_process_id` (`process_id`),
  CONSTRAINT `FK_processname_base_process_id` FOREIGN KEY (`process_id`) REFERENCES `process` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE process DROP locationName;

DROP TABLE IF EXISTS `processname_location`;

CREATE TABLE `processname_location` (
  `process_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_processname_location_process_id` (`process_id`),
  CONSTRAINT `FK_processname_location_process_id` FOREIGN KEY (`process_id`) REFERENCES `process` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE process DROP routeName;

DROP TABLE IF EXISTS `processname_route`;

CREATE TABLE `processname_route` (
  `process_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_processname_route_process_id` (`process_id`),
  CONSTRAINT `FK_processname_route_process_id` FOREIGN KEY (`process_id`) REFERENCES `process` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE process DROP unitName;

DROP TABLE IF EXISTS `processname_unit`;

CREATE TABLE `processname_unit` (
  `process_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_processname_unit_process_id` (`process_id`),
  CONSTRAINT `FK_processname_unit_process_id` FOREIGN KEY (`process_id`) REFERENCES `process` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



ALTER TABLE quantitativereference DROP otherReference;


DROP TABLE IF EXISTS `process_methods`;

DROP TABLE IF EXISTS `review_methods`;

CREATE TABLE `review_methods` (
  `scopeofreview_id` bigint(20) DEFAULT NULL,
  `method` varchar(255) DEFAULT NULL,
  KEY `FK_review_methods_scopeofreview_id` (`scopeofreview_id`),
  CONSTRAINT `FK_review_methods_scopeofreview_id` FOREIGN KEY (`scopeofreview_id`) REFERENCES `scopeofreview` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE review DROP otherDetails;

DROP TABLE IF EXISTS `review_otherdetails`;

DROP TABLE IF EXISTS `review_otherreviewdetails`;

CREATE TABLE `review_otherreviewdetails` (
  `review_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_review_otherReviewDetails_review_id` (`review_id`),
  CONSTRAINT `FK_review_otherReviewDetails_review_id` FOREIGN KEY (`review_id`) REFERENCES `review` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



ALTER TABLE review DROP reviewDetails;

DROP TABLE IF EXISTS `review_reviewdetails`;

CREATE TABLE `review_reviewdetails` (
  `review_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_review_reviewDetails_review_id` (`review_id`),
  CONSTRAINT `FK_review_reviewDetails_review_id` FOREIGN KEY (`review_id`) REFERENCES `review` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/* source */
ALTER TABLE source DROP name;

DROP TABLE IF EXISTS `source_name`;

CREATE TABLE `source_name` (
  `source_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_Source_NAME_source_id` (`source_id`),
  CONSTRAINT `FK_Source_NAME_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



ALTER TABLE source DROP shortName;

DROP TABLE IF EXISTS `source_shortname`;

CREATE TABLE `source_shortname` (
  `source_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_source_shortName_source_id` (`source_id`),
  CONSTRAINT `FK_source_shortName_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



ALTER TABLE source DROP citation;

DROP TABLE IF EXISTS `source_citation`;

CREATE TABLE `source_citation` (
  `source_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_source_citation_source_id` (`source_id`),
  CONSTRAINT `FK_source_citation_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



ALTER TABLE source DROP description;

DROP TABLE IF EXISTS `source_description`;

CREATE TABLE `source_description` (
  `source_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_Source_DESCRIPTION_source_id` (`source_id`),
  CONSTRAINT `FK_Source_DESCRIPTION_source_id` FOREIGN KEY (`source_id`) REFERENCES `source` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




/* unitgroup */
ALTER TABLE unit DROP description;

DROP TABLE IF EXISTS `unit_description`;

CREATE TABLE `unit_description` (
  `unit_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_unit_description_unit_id` (`unit_id`),
  CONSTRAINT `FK_unit_description_unit_id` FOREIGN KEY (`unit_id`) REFERENCES `unit` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



ALTER TABLE unitgroup DROP description;

DROP TABLE IF EXISTS `unitgroup_description`;

CREATE TABLE `unitgroup_description` (
  `unitgroup_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_UnitGroup_DESCRIPTION_unitgroup_id` (`unitgroup_id`),
  CONSTRAINT `FK_UnitGroup_DESCRIPTION_unitgroup_id` FOREIGN KEY (`unitgroup_id`) REFERENCES `unitgroup` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



ALTER TABLE unitgroup DROP name;

DROP TABLE IF EXISTS `unitgroup_name`;

CREATE TABLE `unitgroup_name` (
  `unitgroup_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `FK_UnitGroup_NAME_unitgroup_id` (`unitgroup_id`),
  CONSTRAINT `FK_UnitGroup_NAME_unitgroup_id` FOREIGN KEY (`unitgroup_id`) REFERENCES `unitgroup` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/* globalref */
ALTER TABLE globalreference DROP defaultvalue;

DROP TABLE IF EXISTS `globalreference_shortdescription`;

CREATE TABLE `globalreference_shortdescription` (
  `globalreference_id` bigint(20) DEFAULT NULL,
  `value` text,
  `lang` varchar(255) DEFAULT NULL,
  KEY `globalreference_shortDescriptionglobalreference_id` (`globalreference_id`),
  CONSTRAINT `globalreference_shortDescriptionglobalreference_id` FOREIGN KEY (`globalreference_id`) REFERENCES `globalreference` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;










