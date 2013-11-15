-- -----------------------------------------------------------------------------
-- Copyright (c) 2011 Karlsruhe Institute of Technology (KIT) - Institute for 
-- Applied Computer Science (IAI). 
-- 
-- This file is part of soda4LCA - the Service-Oriented Life Cycle Data Store.
-- 
-- soda4LCA is free software: you can redistribute it and/or modify
-- it under the terms of the GNU General Public License as published by the 
-- Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.
-- 
-- soda4LCA is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
-- 
-- You should have received a copy of the GNU General Public License
-- along with soda4LCA.  If not, see <http://www.gnu.org/licenses/>.
-- -----------------------------------------------------------------------------

-- phpMyAdmin SQL Dump
-- version 3.3.9
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Sep 27, 2011 at 09:32 
-- Server version: 5.5.8
-- PHP Version: 5.3.5

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- Database: `soda4lca`
--

-- --------------------------------------------------------

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `DATASETTYPE` varchar(255) DEFAULT NULL,
  `CATNAME` varchar(255) DEFAULT NULL,
  `CATID` varchar(255) DEFAULT NULL,
  `CATLEVEL` int(11) DEFAULT NULL,
  `UUID` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `categorysystem`
--

DROP TABLE IF EXISTS `categorysystem`;
CREATE TABLE `categorysystem` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `csname` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `categorysystem_category`
--

DROP TABLE IF EXISTS `categorysystem_category`;
CREATE TABLE `categorysystem_category` (
  `CategorySystem_ID` bigint(20) NOT NULL,
  `categories_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`CategorySystem_ID`,`categories_ID`),
  KEY `FK_CATEGORYSYSTEM_CATEGORY_categories_ID` (`categories_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `classification`
--

DROP TABLE IF EXISTS `classification`;
CREATE TABLE `classification` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `clname` varchar(255) DEFAULT NULL,
  `catSystem` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_CLASSIFICATION_catSystem` (`catSystem`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `classification_clclass`
--

DROP TABLE IF EXISTS `classification_clclass`;
CREATE TABLE `classification_clclass` (
  `Classification_ID` bigint(20) NOT NULL,
  `classes_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`Classification_ID`,`classes_ID`),
  KEY `FK_CLASSIFICATION_CLCLASS_classes_ID` (`classes_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `clclass`
--

DROP TABLE IF EXISTS `clclass`;
CREATE TABLE `clclass` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `DATASETTYPE` varchar(255) DEFAULT NULL,
  `clLevel` int(11) DEFAULT NULL,
  `clName` varchar(255) DEFAULT NULL,
  `CLID` varchar(255) DEFAULT NULL,
  `CATEGORY_ID` bigint(20) DEFAULT NULL,
  `UUID` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_CLCLASS_CATEGORY_ID` (`CATEGORY_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `compliancesystem`
--

DROP TABLE IF EXISTS `compliancesystem`;
CREATE TABLE `compliancesystem` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `OVERALLCOMPLIANCE` varchar(255) DEFAULT NULL,
  `DOCUMENTATIONCOMPLIANCE` varchar(255) DEFAULT NULL,
  `METHODOLOGICALCOMPLIANCE` varchar(255) DEFAULT NULL,
  `QUALITYCOMPLIANCE` varchar(255) DEFAULT NULL,
  `NOMENCLATURECOMPLIANCE` varchar(255) DEFAULT NULL,
  `REVIEWCOMPLIANCE` varchar(255) DEFAULT NULL,
  `SOURCEREFERENCE_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_COMPLIANCESYSTEM_SOURCEREFERENCE_ID` (`SOURCEREFERENCE_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `contact`
--

DROP TABLE IF EXISTS `contact`;
CREATE TABLE `contact` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `RELEASESTATE` varchar(255) DEFAULT NULL,
  `PHONE` varchar(255) DEFAULT NULL,
  `FAX` varchar(255) DEFAULT NULL,
  `CENTRALCONTACTPOINT` varchar(255) DEFAULT NULL,
  `PERMANENTURI` varchar(255) DEFAULT NULL,
  `WWW` varchar(255) DEFAULT NULL,
  `EMAIL` varchar(255) DEFAULT NULL,
  `BRANCH` int(11) DEFAULT NULL,
  `CONTACTADDRESS` varchar(255) DEFAULT NULL,
  `CLASSIFICATION_ID` bigint(20) DEFAULT NULL,
  `XMLFILE_ID` bigint(20) DEFAULT NULL,
  `UUID` varchar(255) DEFAULT NULL,
  `shortName` varchar(255) DEFAULT NULL,
  `description` text,
  `name` varchar(255) DEFAULT NULL,
  `MAJORVERSION` int(11) DEFAULT NULL,
  `MINORVERSION` int(11) DEFAULT NULL,
  `SUBMINORVERSION` int(11) DEFAULT NULL,
  `mostRecentVersion` tinyint(1) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UNQ_CONTACT_0` (`UUID`,`MAJORVERSION`,`MINORVERSION`,`SUBMINORVERSION`),
  KEY `FK_CONTACT_XMLFILE_ID` (`XMLFILE_ID`),
  KEY `FK_CONTACT_CLASSIFICATION_ID` (`CLASSIFICATION_ID`),
  KEY `mostRecentVersion` (`mostRecentVersion`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `contact_shortname`
--

DROP TABLE IF EXISTS `contact_shortname`;
CREATE TABLE `contact_shortname` (
  `contact_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`contact_id`,`lStringMap_ID`),
  KEY `FK_contact_shortname_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `dataqualityindicator`
--

DROP TABLE IF EXISTS `dataqualityindicator`;
CREATE TABLE `dataqualityindicator` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `indicatorName` varchar(255) DEFAULT NULL,
  `indicatorValue` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `dataset_descriptions`
--

DROP TABLE IF EXISTS `dataset_descriptions`;
CREATE TABLE `dataset_descriptions` (
  `dataset_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`dataset_id`,`lStringMap_ID`),
  KEY `FK_dataset_descriptions_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `dataset_names`
--

DROP TABLE IF EXISTS `dataset_names`;
CREATE TABLE `dataset_names` (
  `dataset_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`dataset_id`,`lStringMap_ID`),
  KEY `FK_dataset_names_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `datastock`
--

DROP TABLE IF EXISTS `datastock`;
CREATE TABLE `datastock` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` text,
  `title` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `datastock_contact`
--

DROP TABLE IF EXISTS `datastock_contact`;
CREATE TABLE `datastock_contact` (
  `DataStock_ID` bigint(20) NOT NULL,
  `contacts_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`DataStock_ID`,`contacts_ID`),
  KEY `FK_DATASTOCK_CONTACT_contacts_ID` (`contacts_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `datastock_descriptions`
--

DROP TABLE IF EXISTS `datastock_descriptions`;
CREATE TABLE `datastock_descriptions` (
  `datastock_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`datastock_id`,`lStringMap_ID`),
  KEY `FK_datastock_descriptions_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `datastock_flow`
--

DROP TABLE IF EXISTS `datastock_flow`;
CREATE TABLE `datastock_flow` (
  `DataStock_ID` bigint(20) NOT NULL,
  `flows_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`DataStock_ID`,`flows_ID`),
  KEY `FK_DATASTOCK_FLOW_flows_ID` (`flows_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `datastock_flowproperty`
--

DROP TABLE IF EXISTS `datastock_flowproperty`;
CREATE TABLE `datastock_flowproperty` (
  `DataStock_ID` bigint(20) NOT NULL,
  `flowProperties_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`DataStock_ID`,`flowProperties_ID`),
  KEY `FK_DATASTOCK_FLOWPROPERTY_flowProperties_ID` (`flowProperties_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `datastock_names`
--

DROP TABLE IF EXISTS `datastock_names`;
CREATE TABLE `datastock_names` (
  `datastock_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`datastock_id`,`lStringMap_ID`),
  KEY `FK_datastock_names_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `datastock_process`
--

DROP TABLE IF EXISTS `datastock_process`;
CREATE TABLE `datastock_process` (
  `processes_ID` bigint(20) NOT NULL,
  `containingDataStocks_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`processes_ID`,`containingDataStocks_ID`),
  KEY `FK_DATASTOCK_PROCESS_containingDataStocks_ID` (`containingDataStocks_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `datastock_source`
--

DROP TABLE IF EXISTS `datastock_source`;
CREATE TABLE `datastock_source` (
  `DataStock_ID` bigint(20) NOT NULL,
  `sources_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`DataStock_ID`,`sources_ID`),
  KEY `FK_DATASTOCK_SOURCE_sources_ID` (`sources_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `datastock_titles`
--

DROP TABLE IF EXISTS `datastock_titles`;
CREATE TABLE `datastock_titles` (
  `datastock_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`datastock_id`,`lStringMap_ID`),
  KEY `FK_datastock_titles_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `datastock_unitgroup`
--

DROP TABLE IF EXISTS `datastock_unitgroup`;
CREATE TABLE `datastock_unitgroup` (
  `DataStock_ID` bigint(20) NOT NULL,
  `unitGroups_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`DataStock_ID`,`unitGroups_ID`),
  KEY `FK_DATASTOCK_UNITGROUP_unitGroups_ID` (`unitGroups_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `digitalfile`
--

DROP TABLE IF EXISTS `digitalfile`;
CREATE TABLE `digitalfile` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `FILENAME` varchar(1000) DEFAULT NULL,
  `SOURCE_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_DIGITALFILE_SOURCE_ID` (`SOURCE_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `exchange`
--

DROP TABLE IF EXISTS `exchange`;
CREATE TABLE `exchange` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `INTERNALID` int(11) DEFAULT NULL,
  `DERIVATIONTYPE` varchar(255) DEFAULT NULL,
  `DATASOURCE` varchar(255) DEFAULT NULL,
  `MEANAMOUNT` float DEFAULT NULL,
  `LOCATION` varchar(255) DEFAULT NULL,
  `MAXIMUMAMOUNT` float DEFAULT NULL,
  `UNCERTAINTYDISTRIBUTION` varchar(255) DEFAULT NULL,
  `FUNCTIONTYPE` varchar(255) DEFAULT NULL,
  `RESULTINGAMOUNT` float DEFAULT NULL,
  `STANDARDDEVIATION` float DEFAULT NULL,
  `ALLOCATION` varchar(255) DEFAULT NULL,
  `REFERENCETOVARIABLE` varchar(255) DEFAULT NULL,
  `MINIMUMAMOUNT` float DEFAULT NULL,
  `EXCHANGEDIRECTION` varchar(255) DEFAULT NULL,
  `REFTODATASOURCE_ID` bigint(20) DEFAULT NULL,
  `FLOW_ID` bigint(20) DEFAULT NULL,
  `FLOWREFERENCE_ID` bigint(20) DEFAULT NULL,
  `comment` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_EXCHANGE_FLOWREFERENCE_ID` (`FLOWREFERENCE_ID`),
  KEY `FK_EXCHANGE_FLOW_ID` (`FLOW_ID`),
  KEY `FK_EXCHANGE_REFTODATASOURCE_ID` (`REFTODATASOURCE_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `exchange_comment`
--

DROP TABLE IF EXISTS `exchange_comment`;
CREATE TABLE `exchange_comment` (
  `exchange_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`exchange_id`,`lStringMap_ID`),
  KEY `FK_exchange_comment_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `flow`
--

DROP TABLE IF EXISTS `flow`;
CREATE TABLE `flow` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `RELEASESTATE` varchar(255) DEFAULT NULL,
  `PERMANENTURI` varchar(255) DEFAULT NULL,
  `CASNUMBER` varchar(255) DEFAULT NULL,
  `TYPE` varchar(255) DEFAULT NULL,
  `SUMFORMULA` varchar(255) DEFAULT NULL,
  `BRANCH` int(11) DEFAULT NULL,
  `XMLFILE_ID` bigint(20) DEFAULT NULL,
  `CLASSIFICATION_ID` bigint(20) DEFAULT NULL,
  `CATEGORIZATION_ID` bigint(20) DEFAULT NULL,
  `REFERENCEPROPERTY_ID` bigint(20) DEFAULT NULL,
  `description` text,
  `MAJORVERSION` int(11) DEFAULT NULL,
  `MINORVERSION` int(11) DEFAULT NULL,
  `SUBMINORVERSION` int(11) DEFAULT NULL,
  `synonyms` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `UUID` varchar(255) DEFAULT NULL,
  `mostRecentVersion` tinyint(1) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UNQ_FLOW_0` (`UUID`,`MAJORVERSION`,`MINORVERSION`,`SUBMINORVERSION`),
  KEY `FK_FLOW_CLASSIFICATION_ID` (`CLASSIFICATION_ID`),
  KEY `FK_FLOW_CATEGORIZATION_ID` (`CATEGORIZATION_ID`),
  KEY `FK_FLOW_XMLFILE_ID` (`XMLFILE_ID`),
  KEY `FK_FLOW_REFERENCEPROPERTY_ID` (`REFERENCEPROPERTY_ID`),
  KEY `mostRecentVersion` (`mostRecentVersion`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `flowpropdesc_descriptions`
--

DROP TABLE IF EXISTS `flowpropdesc_descriptions`;
CREATE TABLE `flowpropdesc_descriptions` (
  `flowproperty_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`flowproperty_id`,`lStringMap_ID`),
  KEY `FK_flowpropdesc_descriptions_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `flowproperty`
--

DROP TABLE IF EXISTS `flowproperty`;
CREATE TABLE `flowproperty` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `RELEASESTATE` varchar(255) DEFAULT NULL,
  `PERMANENTURI` varchar(255) DEFAULT NULL,
  `BRANCH` int(11) DEFAULT NULL,
  `XMLFILE_ID` bigint(20) DEFAULT NULL,
  `REFERENCETOUNITGROUP_ID` bigint(20) DEFAULT NULL,
  `UNITGROUP_ID` bigint(20) DEFAULT NULL,
  `CLASSIFICATION_ID` bigint(20) DEFAULT NULL,
  `UUID` varchar(255) DEFAULT NULL,
  `MAJORVERSION` int(11) DEFAULT NULL,
  `MINORVERSION` int(11) DEFAULT NULL,
  `SUBMINORVERSION` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` text,
  `synonyms` varchar(255) DEFAULT NULL,
  `mostRecentVersion` tinyint(1) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UNQ_FLOWPROPERTY_0` (`UUID`,`MAJORVERSION`,`MINORVERSION`,`SUBMINORVERSION`),
  KEY `FK_FLOWPROPERTY_XMLFILE_ID` (`XMLFILE_ID`),
  KEY `FK_FLOWPROPERTY_CLASSIFICATION_ID` (`CLASSIFICATION_ID`),
  KEY `FK_FLOWPROPERTY_REFERENCETOUNITGROUP_ID` (`REFERENCETOUNITGROUP_ID`),
  KEY `FK_FLOWPROPERTY_UNITGROUP_ID` (`UNITGROUP_ID`),
  KEY `mostRecentVersion` (`mostRecentVersion`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `flowpropertydescription`
--

DROP TABLE IF EXISTS `flowpropertydescription`;
CREATE TABLE `flowpropertydescription` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `INTERNALID` int(11) DEFAULT NULL,
  `MEANVALUE` double DEFAULT NULL,
  `DERIVATIONTYPE` varchar(255) DEFAULT NULL,
  `MINVALUE` double DEFAULT NULL,
  `STANDARDDEVIATION` float DEFAULT NULL,
  `maximumValue` double DEFAULT NULL,
  `UNCERTAINTYTYPE` varchar(255) DEFAULT NULL,
  `FLOWPROPERTY_ID` bigint(20) DEFAULT NULL,
  `FLOWPROPERTYREF_ID` bigint(20) DEFAULT NULL,
  `description` text,
  PRIMARY KEY (`ID`),
  KEY `FK_FLOWPROPERTYDESCRIPTION_FLOWPROPERTY_ID` (`FLOWPROPERTY_ID`),
  KEY `FK_FLOWPROPERTYDESCRIPTION_FLOWPROPERTYREF_ID` (`FLOWPROPERTYREF_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `flowproperty_synonyms`
--

DROP TABLE IF EXISTS `flowproperty_synonyms`;
CREATE TABLE `flowproperty_synonyms` (
  `flowproperty_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`flowproperty_id`,`lStringMap_ID`),
  KEY `FK_flowproperty_synonyms_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `flow_flowpropertydescription`
--

DROP TABLE IF EXISTS `flow_flowpropertydescription`;
CREATE TABLE `flow_flowpropertydescription` (
  `Flow_ID` bigint(20) NOT NULL,
  `propertyDescriptions_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`Flow_ID`,`propertyDescriptions_ID`),
  KEY `FLOWFLOWPROPERTYDESCRIPTIONpropertyDescriptions_ID` (`propertyDescriptions_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `flow_synonyms`
--

DROP TABLE IF EXISTS `flow_synonyms`;
CREATE TABLE `flow_synonyms` (
  `flow_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`flow_id`,`lStringMap_ID`),
  KEY `FK_flow_synonyms_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `geographicalarea`
--

DROP TABLE IF EXISTS `geographicalarea`;
CREATE TABLE `geographicalarea` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `AREACODE` varchar(255) DEFAULT NULL,
  `areaName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=270 ;

-- --------------------------------------------------------

--
-- Table structure for table `globalreference`
--

DROP TABLE IF EXISTS `globalreference`;
CREATE TABLE `globalreference` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `TYPE` varchar(255) DEFAULT NULL,
  `URI` varchar(255) DEFAULT NULL,
  `UUID` varchar(255) DEFAULT NULL,
  `MAJORVERSION` int(11) DEFAULT NULL,
  `MINORVERSION` int(11) DEFAULT NULL,
  `SUBMINORVERSION` int(11) DEFAULT NULL,
  `DEFAULTVALUE` text,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `globalreference_subreferences`
--

DROP TABLE IF EXISTS `globalreference_subreferences`;
CREATE TABLE `globalreference_subreferences` (
  `GlobalReference_ID` bigint(20) NOT NULL,
  `SUBREFERENCES` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `industrialsector`
--

DROP TABLE IF EXISTS `industrialsector`;
CREATE TABLE `industrialsector` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `SECTOR` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `SECTOR` (`SECTOR`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=10 ;

-- --------------------------------------------------------

--
-- Table structure for table `languages`
--

DROP TABLE IF EXISTS `languages`;
CREATE TABLE `languages` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `LANGUAGECODE` varchar(255) DEFAULT NULL,
  `NAME` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=140 ;

-- --------------------------------------------------------

--
-- Table structure for table `lciamethod`
--

DROP TABLE IF EXISTS `lciamethod`;
CREATE TABLE `lciamethod` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `RELEASESTATE` varchar(255) DEFAULT NULL,
  `PERMANENTURI` varchar(255) DEFAULT NULL,
  `TYPE` varchar(255) DEFAULT NULL,
  `BRANCH` int(11) DEFAULT NULL,
  `IMPACTINDICATOR` varchar(500) DEFAULT NULL,
  `XMLFILE_ID` bigint(20) DEFAULT NULL,
  `CLASSIFICATION_ID` bigint(20) DEFAULT NULL,
  `UUID` varchar(255) DEFAULT NULL,
  `description` text,
  `durationDescription` varchar(255) DEFAULT NULL,
  `referenceYearDescription` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `MAJORVERSION` int(11) DEFAULT NULL,
  `MINORVERSION` int(11) DEFAULT NULL,
  `SUBMINORVERSION` int(11) DEFAULT NULL,
  `mostRecentVersion` tinyint(1) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UNQ_LCIAMETHOD_0` (`UUID`,`MAJORVERSION`,`MINORVERSION`,`SUBMINORVERSION`),
  KEY `FK_LCIAMETHOD_XMLFILE_ID` (`XMLFILE_ID`),
  KEY `FK_LCIAMETHOD_CLASSIFICATION_ID` (`CLASSIFICATION_ID`),
  KEY `mostRecentVersion` (`mostRecentVersion`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `lciamethodcharacterisationfactor`
--

DROP TABLE IF EXISTS `lciamethodcharacterisationfactor`;
CREATE TABLE `lciamethodcharacterisationfactor` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `MEANVALUE` float DEFAULT NULL,
  `DATADERIVATIONTYPESTATUS` varchar(255) DEFAULT NULL,
  `EXCHANGEDIRECTION` varchar(255) DEFAULT NULL,
  `REFERENCEDFLOWINSTANCE_ID` bigint(20) DEFAULT NULL,
  `FLOWGLOBALREFERENCE_ID` bigint(20) DEFAULT NULL,
  `shortDescription` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `LCMTHODCHARACTERISATIONFACTORRFRNCEDFLOWINSTANCEID` (`REFERENCEDFLOWINSTANCE_ID`),
  KEY `LCMETHODCHARACTERISATIONFACTORFLWGLOBALREFERENCEID` (`FLOWGLOBALREFERENCE_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `lciamethod_areaofprotection`
--

DROP TABLE IF EXISTS `lciamethod_areaofprotection`;
CREATE TABLE `lciamethod_areaofprotection` (
  `LCIAMethod_ID` bigint(20) NOT NULL,
  `AREAOFPROTECTION` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `lciamethod_charfac_shortdescription`
--

DROP TABLE IF EXISTS `lciamethod_charfac_shortdescription`;
CREATE TABLE `lciamethod_charfac_shortdescription` (
  `lciamethod_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`lciamethod_id`,`lStringMap_ID`),
  KEY `lciamethod_charfac_shortDescription_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `lciamethod_durationdescription`
--

DROP TABLE IF EXISTS `lciamethod_durationdescription`;
CREATE TABLE `lciamethod_durationdescription` (
  `lciamethod_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`lciamethod_id`,`lStringMap_ID`),
  KEY `FK_lciamethod_durationDescription_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `lciamethod_impactcategory`
--

DROP TABLE IF EXISTS `lciamethod_impactcategory`;
CREATE TABLE `lciamethod_impactcategory` (
  `LCIAMethod_ID` bigint(20) NOT NULL,
  `IMPACTCATEGORY` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `lciamethod_lciamethodcharacterisationfactor`
--

DROP TABLE IF EXISTS `lciamethod_lciamethodcharacterisationfactor`;
CREATE TABLE `lciamethod_lciamethodcharacterisationfactor` (
  `LCIAMethod_ID` bigint(20) NOT NULL,
  `charactarisationFactors_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`LCIAMethod_ID`,`charactarisationFactors_ID`),
  KEY `LCMTHDLCMTHDCHARACTERISATIONFACTORchrctrstnFctrsID` (`charactarisationFactors_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `lciamethod_methodology`
--

DROP TABLE IF EXISTS `lciamethod_methodology`;
CREATE TABLE `lciamethod_methodology` (
  `LCIAMethod_ID` bigint(20) NOT NULL,
  `METHODOLOGY` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `lciamethod_referenceyeardescription`
--

DROP TABLE IF EXISTS `lciamethod_referenceyeardescription`;
CREATE TABLE `lciamethod_referenceyeardescription` (
  `lciamethod_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`lciamethod_id`,`lStringMap_ID`),
  KEY `lciamethod_referenceYearDescription_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `lciaresult`
--

DROP TABLE IF EXISTS `lciaresult`;
CREATE TABLE `lciaresult` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `UNCERTAINTYDISTRIBUTION` varchar(255) DEFAULT NULL,
  `MEANAMOUNT` float DEFAULT NULL,
  `STANDARDDEVIATION` float DEFAULT NULL,
  `METHODREFERENCE_ID` bigint(20) DEFAULT NULL,
  `comment` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_LCIARESULT_METHODREFERENCE_ID` (`METHODREFERENCE_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `lcimethodinformation`
--

DROP TABLE IF EXISTS `lcimethodinformation`;
CREATE TABLE `lcimethodinformation` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `METHODPRINCIPLE` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `lstring`
--

DROP TABLE IF EXISTS `lstring`;
CREATE TABLE `lstring` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `strValue` varchar(255) DEFAULT NULL,
  `lang` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `ltext`
--

DROP TABLE IF EXISTS `ltext`;
CREATE TABLE `ltext` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `text` varchar(10000) DEFAULT NULL,
  `lang` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `networknode`
--

DROP TABLE IF EXISTS `networknode`;
CREATE TABLE `networknode` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `BASEURL` varchar(255) NOT NULL,
  `NODEID` varchar(255) NOT NULL,
  `ACCESSPASSWORD` varchar(255) DEFAULT NULL,
  `ADMINNAME` varchar(255) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `NAME` varchar(255) NOT NULL,
  `ADMINEMAILADDRESS` varchar(255) DEFAULT NULL,
  `ACCESSACCOUNT` varchar(255) DEFAULT NULL,
  `ADMINPHONE` varchar(255) DEFAULT NULL,
  `OPERATOR` varchar(255) DEFAULT NULL,
  `ADMINWWWADDRESS` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `BASEURL` (`BASEURL`),
  UNIQUE KEY `NODEID` (`NODEID`),
  UNIQUE KEY `NAME` (`NAME`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `process`
--

DROP TABLE IF EXISTS `process`;
CREATE TABLE `process` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `PARAMETERIZED` tinyint(1) DEFAULT '0',
  `TYPE` varchar(255) DEFAULT NULL,
  `COMPLETENESS` varchar(255) DEFAULT NULL,
  `RELEASESTATE` varchar(255) DEFAULT NULL,
  `PERMANENTURI` varchar(255) DEFAULT NULL,
  `FORMAT` varchar(255) DEFAULT NULL,
  `BRANCH` int(11) DEFAULT NULL,
  `RESULTSINCLUDED` tinyint(1) DEFAULT '0',
  `EXCHANGESINCLUDED` tinyint(1) DEFAULT '0',
  `APPROVEDBY_ID` bigint(20) DEFAULT NULL,
  `LCIMETHODINFORMATION_ID` bigint(20) DEFAULT NULL,
  `XMLFILE_ID` bigint(20) DEFAULT NULL,
  `OWNERREFERENCE_ID` bigint(20) DEFAULT NULL,
  `CLASSIFICATION_ID` bigint(20) DEFAULT NULL,
  `INTERNALREFERENCE_ID` bigint(20) DEFAULT NULL,
  `COPYRIGHT` tinyint(1) DEFAULT '0',
  `LICENSETYPE` varchar(255) DEFAULT NULL,
  `useRestrictions` text,
  `MAJORVERSION` int(11) DEFAULT NULL,
  `MINORVERSION` int(11) DEFAULT NULL,
  `SUBMINORVERSION` int(11) DEFAULT NULL,
  `synonyms` varchar(255) DEFAULT NULL,
  `UUID` varchar(255) DEFAULT NULL,
  `locationName` varchar(255) DEFAULT NULL,
  `baseName` varchar(255) DEFAULT NULL,
  `unitName` varchar(255) DEFAULT NULL,
  `routeName` varchar(255) DEFAULT NULL,
  `technicalPurpose` text,
  `name` varchar(255) DEFAULT NULL,
  `useAdvice` text,
  `VALIDUNTIL` int(11) DEFAULT NULL,
  `REFERENCEYEAR` int(11) DEFAULT NULL,
  `timeDescription` text,
  `description` text,
  `LOCATION` varchar(255) DEFAULT NULL,
  `locationRestriction` text,
  `mostRecentVersion` tinyint(1) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UNQ_PROCESS_0` (`UUID`,`MAJORVERSION`,`MINORVERSION`,`SUBMINORVERSION`),
  KEY `FK_PROCESS_LCIMETHODINFORMATION_ID` (`LCIMETHODINFORMATION_ID`),
  KEY `FK_PROCESS_OWNERREFERENCE_ID` (`OWNERREFERENCE_ID`),
  KEY `FK_PROCESS_APPROVEDBY_ID` (`APPROVEDBY_ID`),
  KEY `FK_PROCESS_XMLFILE_ID` (`XMLFILE_ID`),
  KEY `FK_PROCESS_INTERNALREFERENCE_ID` (`INTERNALREFERENCE_ID`),
  KEY `FK_PROCESS_CLASSIFICATION_ID` (`CLASSIFICATION_ID`),
  KEY `mostRecentVersion` (`mostRecentVersion`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `processname_base`
--

DROP TABLE IF EXISTS `processname_base`;
CREATE TABLE `processname_base` (
  `process_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`process_id`,`lStringMap_ID`),
  KEY `FK_processname_base_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `processname_location`
--

DROP TABLE IF EXISTS `processname_location`;
CREATE TABLE `processname_location` (
  `process_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`process_id`,`lStringMap_ID`),
  KEY `FK_processname_location_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `processname_route`
--

DROP TABLE IF EXISTS `processname_route`;
CREATE TABLE `processname_route` (
  `process_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`process_id`,`lStringMap_ID`),
  KEY `FK_processname_route_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `processname_unit`
--

DROP TABLE IF EXISTS `processname_unit`;
CREATE TABLE `processname_unit` (
  `process_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`process_id`,`lStringMap_ID`),
  KEY `FK_processname_unit_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `process_allocations`
--

DROP TABLE IF EXISTS `process_allocations`;
CREATE TABLE `process_allocations` (
  `processId` bigint(20) NOT NULL,
  `approach` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `process_compliancesystem`
--

DROP TABLE IF EXISTS `process_compliancesystem`;
CREATE TABLE `process_compliancesystem` (
  `Process_ID` bigint(20) NOT NULL,
  `complianceSystems_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`Process_ID`,`complianceSystems_ID`),
  KEY `FK_PROCESS_COMPLIANCESYSTEM_complianceSystems_ID` (`complianceSystems_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `process_exchange`
--

DROP TABLE IF EXISTS `process_exchange`;
CREATE TABLE `process_exchange` (
  `Process_ID` bigint(20) NOT NULL,
  `exchanges_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`Process_ID`,`exchanges_ID`),
  KEY `FK_PROCESS_EXCHANGE_exchanges_ID` (`exchanges_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `process_lciaresult`
--

DROP TABLE IF EXISTS `process_lciaresult`;
CREATE TABLE `process_lciaresult` (
  `Process_ID` bigint(20) NOT NULL,
  `lciaResults_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`Process_ID`,`lciaResults_ID`),
  KEY `FK_PROCESS_LCIARESULT_lciaResults_ID` (`lciaResults_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `process_locationrestriction`
--

DROP TABLE IF EXISTS `process_locationrestriction`;
CREATE TABLE `process_locationrestriction` (
  `process_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`process_id`,`lStringMap_ID`),
  KEY `FK_process_locationRestriction_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `process_methods`
--

DROP TABLE IF EXISTS `process_methods`;
CREATE TABLE `process_methods` (
  `processId` bigint(20) NOT NULL,
  `method` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `process_otherreference`
--

DROP TABLE IF EXISTS `process_otherreference`;
CREATE TABLE `process_otherreference` (
  `process_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`process_id`,`lStringMap_ID`),
  KEY `FK_process_otherReference_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `process_purpose`
--

DROP TABLE IF EXISTS `process_purpose`;
CREATE TABLE `process_purpose` (
  `process_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`process_id`,`lStringMap_ID`),
  KEY `FK_process_purpose_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `process_referenceids`
--

DROP TABLE IF EXISTS `process_referenceids`;
CREATE TABLE `process_referenceids` (
  `processId` bigint(20) NOT NULL,
  `refId` int(11) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `process_review`
--

DROP TABLE IF EXISTS `process_review`;
CREATE TABLE `process_review` (
  `Process_ID` bigint(20) NOT NULL,
  `reviews_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`Process_ID`,`reviews_ID`),
  KEY `FK_PROCESS_REVIEW_reviews_ID` (`reviews_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `process_synonyms`
--

DROP TABLE IF EXISTS `process_synonyms`;
CREATE TABLE `process_synonyms` (
  `process_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`process_id`,`lStringMap_ID`),
  KEY `FK_process_synonyms_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `process_timedescription`
--

DROP TABLE IF EXISTS `process_timedescription`;
CREATE TABLE `process_timedescription` (
  `process_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`process_id`,`lStringMap_ID`),
  KEY `FK_process_timeDescription_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `process_useadvice`
--

DROP TABLE IF EXISTS `process_useadvice`;
CREATE TABLE `process_useadvice` (
  `process_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`process_id`,`lStringMap_ID`),
  KEY `FK_process_useAdvice_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `process_userestrictions`
--

DROP TABLE IF EXISTS `process_userestrictions`;
CREATE TABLE `process_userestrictions` (
  `process_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`process_id`,`lStringMap_ID`),
  KEY `FK_process_useRestrictions_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `quantitativereference`
--

DROP TABLE IF EXISTS `quantitativereference`;
CREATE TABLE `quantitativereference` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `TYPE` varchar(255) DEFAULT NULL,
  `otherReference` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `TYPE` varchar(255) DEFAULT NULL,
  `reviewDetails` text,
  `otherDetails` text,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `review_dataqualityindicator`
--

DROP TABLE IF EXISTS `review_dataqualityindicator`;
CREATE TABLE `review_dataqualityindicator` (
  `Review_ID` bigint(20) NOT NULL,
  `qualityIndicators_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`Review_ID`,`qualityIndicators_ID`),
  KEY `REVIEW_DATAQUALITYINDICATOR_qualityIndicators_ID` (`qualityIndicators_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `review_globalreference`
--

DROP TABLE IF EXISTS `review_globalreference`;
CREATE TABLE `review_globalreference` (
  `Review_ID` bigint(20) NOT NULL,
  `referencesToReviewers_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`Review_ID`,`referencesToReviewers_ID`),
  KEY `FK_REVIEW_GLOBALREFERENCE_referencesToReviewers_ID` (`referencesToReviewers_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `review_otherdetails`
--

DROP TABLE IF EXISTS `review_otherdetails`;
CREATE TABLE `review_otherdetails` (
  `review_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`review_id`,`lStringMap_ID`),
  KEY `FK_review_otherDetails_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `review_reviewdetails`
--

DROP TABLE IF EXISTS `review_reviewdetails`;
CREATE TABLE `review_reviewdetails` (
  `review_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`review_id`,`lStringMap_ID`),
  KEY `FK_review_reviewDetails_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `review_scopeofreview`
--

DROP TABLE IF EXISTS `review_scopeofreview`;
CREATE TABLE `review_scopeofreview` (
  `Review_ID` bigint(20) NOT NULL,
  `scopes_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`Review_ID`,`scopes_ID`),
  KEY `FK_REVIEW_SCOPEOFREVIEW_scopes_ID` (`scopes_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `scopeofreview`
--

DROP TABLE IF EXISTS `scopeofreview`;
CREATE TABLE `scopeofreview` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `NAME` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `source`
--

DROP TABLE IF EXISTS `source`;
CREATE TABLE `source` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `RELEASESTATE` varchar(255) DEFAULT NULL,
  `PERMANENTURI` varchar(255) DEFAULT NULL,
  `PUBLICATIONTYPE` varchar(255) DEFAULT NULL,
  `BRANCH` int(11) DEFAULT NULL,
  `XMLFILE_ID` bigint(20) DEFAULT NULL,
  `CLASSIFICATION_ID` bigint(20) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `MAJORVERSION` int(11) DEFAULT NULL,
  `MINORVERSION` int(11) DEFAULT NULL,
  `SUBMINORVERSION` int(11) DEFAULT NULL,
  `shortName` varchar(255) DEFAULT NULL,
  `citation` text,
  `description` text,
  `UUID` varchar(255) DEFAULT NULL,
  `mostRecentVersion` tinyint(1) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UNQ_SOURCE_0` (`UUID`,`MAJORVERSION`,`MINORVERSION`,`SUBMINORVERSION`),
  KEY `FK_SOURCE_CLASSIFICATION_ID` (`CLASSIFICATION_ID`),
  KEY `FK_SOURCE_XMLFILE_ID` (`XMLFILE_ID`),
  KEY `mostRecentVersion` (`mostRecentVersion`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `source_citation`
--

DROP TABLE IF EXISTS `source_citation`;
CREATE TABLE `source_citation` (
  `source_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`source_id`,`lStringMap_ID`),
  KEY `FK_source_citation_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `source_globalreference`
--

DROP TABLE IF EXISTS `source_globalreference`;
CREATE TABLE `source_globalreference` (
  `Source_ID` bigint(20) NOT NULL,
  `contacts_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`Source_ID`,`contacts_ID`),
  KEY `FK_SOURCE_GLOBALREFERENCE_contacts_ID` (`contacts_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `source_shortname`
--

DROP TABLE IF EXISTS `source_shortname`;
CREATE TABLE `source_shortname` (
  `source_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`source_id`,`lStringMap_ID`),
  KEY `FK_source_shortname_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `unit`
--

DROP TABLE IF EXISTS `unit`;
CREATE TABLE `unit` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `INTERNALID` int(11) DEFAULT NULL,
  `MEANVALUE` double DEFAULT NULL,
  `unitname` varchar(255) DEFAULT NULL,
  `description` text,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `unitgroup`
--

DROP TABLE IF EXISTS `unitgroup`;
CREATE TABLE `unitgroup` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `RELEASESTATE` varchar(255) DEFAULT NULL,
  `PERMANENTURI` varchar(255) DEFAULT NULL,
  `BRANCH` int(11) DEFAULT NULL,
  `REFERENCEUNIT_ID` bigint(20) DEFAULT NULL,
  `CLASSIFICATION_ID` bigint(20) DEFAULT NULL,
  `XMLFILE_ID` bigint(20) DEFAULT NULL,
  `UUID` varchar(255) DEFAULT NULL,
  `description` text,
  `MAJORVERSION` int(11) DEFAULT NULL,
  `MINORVERSION` int(11) DEFAULT NULL,
  `SUBMINORVERSION` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `mostRecentVersion` tinyint(1) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UNQ_UNITGROUP_0` (`UUID`,`MAJORVERSION`,`MINORVERSION`,`SUBMINORVERSION`),
  KEY `FK_UNITGROUP_REFERENCEUNIT_ID` (`REFERENCEUNIT_ID`),
  KEY `FK_UNITGROUP_CLASSIFICATION_ID` (`CLASSIFICATION_ID`),
  KEY `FK_UNITGROUP_XMLFILE_ID` (`XMLFILE_ID`),
  KEY `mostRecentVersion` (`mostRecentVersion`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `unitgroup_unit`
--

DROP TABLE IF EXISTS `unitgroup_unit`;
CREATE TABLE `unitgroup_unit` (
  `UnitGroup_ID` bigint(20) NOT NULL,
  `units_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`UnitGroup_ID`,`units_ID`),
  KEY `FK_UNITGROUP_UNIT_units_ID` (`units_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `unit_description`
--

DROP TABLE IF EXISTS `unit_description`;
CREATE TABLE `unit_description` (
  `unit_id` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`unit_id`,`lStringMap_ID`),
  KEY `FK_unit_description_lStringMap_ID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `LASTNAME` varchar(255) DEFAULT NULL,
  `ORGANISATION` varchar(255) DEFAULT NULL,
  `REGISTRATIONKEY` varchar(255) DEFAULT NULL,
  `ORGANISATIONUNIT` varchar(255) DEFAULT NULL,
  `TITLE` varchar(255) DEFAULT NULL,
  `EMAIL` varchar(255) DEFAULT NULL,
  `USERNAME` varchar(255) DEFAULT NULL,
  `GENDER` varchar(255) DEFAULT NULL,
  `FIRSTNAME` varchar(255) DEFAULT NULL,
  `SECTOR_ID` bigint(20) DEFAULT NULL,
  `STREETADDRESS` varchar(255) DEFAULT NULL,
  `ZIPCODE` varchar(255) DEFAULT NULL,
  `COUNTRY` varchar(255) DEFAULT NULL,
  `CITY` varchar(255) DEFAULT NULL,
  `PASSWORD_HASH` varchar(128) DEFAULT NULL,
  `PASSWORD_HASH_SALT` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `USERNAME` (`USERNAME`),
  KEY `FK_USER_SECTOR_ID` (`SECTOR_ID`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

-- --------------------------------------------------------

--
-- Table structure for table `usergroup`
--

DROP TABLE IF EXISTS `usergroup`;
CREATE TABLE `usergroup` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `GROUPNAME` varchar(255) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `GROUPNAME` (`GROUPNAME`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=103 ;

-- --------------------------------------------------------

--
-- Table structure for table `usergroup_roles`
--

DROP TABLE IF EXISTS `usergroup_roles`;
CREATE TABLE `usergroup_roles` (
  `UserGroup_ID` bigint(20) NOT NULL,
  `ROLES` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `usergroup_user`
--

DROP TABLE IF EXISTS `usergroup_user`;
CREATE TABLE `usergroup_user` (
  `groups_ID` bigint(20) NOT NULL,
  `users_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`groups_ID`,`users_ID`),
  KEY `FK_USERGROUP_USER_users_ID` (`users_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles` (
  `User_ID` bigint(20) NOT NULL,
  `ROLES` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `xmlfile`
--

DROP TABLE IF EXISTS `xmlfile`;
CREATE TABLE `xmlfile` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `COMPRESSEDCONTENT` longblob,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `__metamodel_reserved_in_mem_only_table_name_ltext`
--

DROP TABLE IF EXISTS `__metamodel_reserved_in_mem_only_table_name_ltext`;
CREATE TABLE `__metamodel_reserved_in_mem_only_table_name_ltext` (
  `_ID` bigint(20) NOT NULL,
  `lStringMap_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`_ID`,`lStringMap_ID`),
  KEY `MTMODELRESERVEDINMEMONLYTABLENAMELTEXTlStringMapID` (`lStringMap_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



insert  into `geographicalarea`(`ID`,`AREACODE`,`areaName`) values (1,'GLO','Global'),(2,'OCE','Oceanic'),(3,'RAF','Africa'),(4,'RAS','Asia and the Pacific'),(5,'RER','Europe'),(6,'RLA','Latin America & the Caribbean'),(7,'RNA','North America'),(8,'RNE','Near East'),(9,'RME','Middle East'),(10,'EU-15','EU 15'),(11,'EU-NMC','EU new member countries 2004'),(12,'EU-25','EU 25'),(13,'EC-CC','EU candidate countries 2005'),(14,'EU-25&CC','EU 25 plus candidate countries 2005'),(15,'EU-AC','EU associated countries 2005'),(16,'EU-25&CC&AC','EU 25 plus candidate countries 2005 plus associated countries 2005'),(17,'EU-27','EU-27 Member States'),(18,'WEU','Western Europe'),(19,'PAO','Pacific OECD (Japan, Australia, New Zealand)'),(20,'FSU','Independent States of the Former Soviet Union'),(21,'EEU','Central and Eastern Europe'),(22,'MEA','Middle East and North Africa'),(23,'AFR','Sub-Sahara Africa'),(24,'CPA','Centrally Planned Asia and China'),(25,'PAS','Other Pacific Asia'),(26,'SAS','South Asia'),(27,'UCTE','Union for the Co-ordination of Transmission of Electricity '),(28,'CENTREL','Central european power association'),(29,'NORDEL','Nordic countries power association'),(30,'AF','Afghanistan'),(31,'AX','Åland Islands'),(32,'AL','Albania'),(33,'DZ','Algeria'),(34,'AS','American Samoa'),(35,'AD','Andorra'),(36,'AO','Angola'),(37,'AI','Anguilla'),(38,'AQ','Antarctica'),(39,'AG','Antigua and Barbuda'),(40,'AR','Argentina'),(41,'AM','Armenia'),(42,'AW','Aruba'),(43,'AU','Australia'),(44,'AT','Austria'),(45,'AZ','Azerbaijan'),(46,'BS','Bahamas'),(47,'BH','Bahrain'),(48,'BD','Bangladesh'),(49,'BB','Barbados'),(50,'BY','Belarus'),(51,'BE','Belgium'),(52,'BZ','Belize'),(53,'BJ','Benin'),(54,'BM','Bermuda'),(55,'BT','Bhutan'),(56,'BO','Bolivia'),(57,'BA','Bosnia and Herzegovina'),(58,'BW','Botswana'),(59,'BV','Bouvet Island'),(60,'BR','Brazil'),(61,'IO','British Indian Ocean Territory'),(62,'BN','Brunei Darussalam'),(63,'BG','Bulgaria'),(64,'BF','Burkina Faso'),(65,'BI','Burundi'),(66,'KH','Cambodia'),(67,'CM','Cameroon'),(68,'CA','Canada'),(69,'CV','Cape Verde'),(70,'KY','Cayman Islands'),(71,'CF','Central African Republic'),(72,'TD','Chad'),(73,'CL','Chile'),(74,'CN','China'),(75,'CX','Christmas Island'),(76,'CC','Cocos (Keeling) Islands'),(77,'CO','Colombia'),(78,'KM','Comoros'),(79,'CG','Congo'),(80,'CD','Congo, The Democratic Republic Of The'),(81,'CK','Cook Islands'),(82,'CR','Costa Rica'),(83,'CI','Cote D\'ivoire'),(84,'HR','Croatia'),(85,'CU','Cuba'),(86,'CY','Cyprus'),(87,'CZ','Czech Republic'),(88,'DK','Denmark'),(89,'DJ','Djibouti'),(90,'DM','Dominica'),(91,'DO','Dominican Republic'),(92,'EC','Ecuador'),(93,'EG','Egypt'),(94,'SV','El Salvador'),(95,'GQ','Equatorial Guinea'),(96,'ER','Eritrea'),(97,'EE','Estonia'),(98,'ET','Ethiopia'),(99,'FK','Falkland Islands (Malvinas)'),(100,'FO','Faroe Islands'),(101,'FJ','Fiji'),(102,'FI','Finland'),(103,'FR','France'),(104,'GF','French Guiana'),(105,'PF','French Polynesia'),(106,'TF','French Southern Territories'),(107,'GA','Gabon'),(108,'GM','Gambia'),(109,'GE','Georgia'),(110,'DE','Germany'),(111,'GH','Ghana'),(112,'GI','Gibraltar'),(113,'GR','Greece'),(114,'GL','Greenland'),(115,'GD','Grenada'),(116,'GP','Guadeloupe'),(117,'GU','Guam'),(118,'GT','Guatemala'),(119,'GN','Guinea'),(120,'GW','Guinea-bissau'),(121,'GY','Guyana'),(122,'HT','Haiti'),(123,'HM','Heard Island and Mcdonald Islands'),(124,'VA','Holy See (Vatican City State)'),(125,'HN','Honduras'),(126,'HK','Hong Kong'),(127,'HU','Hungary'),(128,'IS','Iceland'),(129,'IN','India'),(130,'ID','Indonesia'),(131,'IR','Iran, Islamic Republic Of'),(132,'IQ','Iraq'),(133,'IE','Ireland'),(134,'IL','Israel'),(135,'IT','Italy'),(136,'JM','Jamaica'),(137,'JP','Japan'),(138,'JO','Jordan'),(139,'KZ','Kazakhstan'),(140,'KE','Kenya'),(141,'KI','Kiribati'),(142,'KP','Korea, Democratic People\'s Republic Of'),(143,'KR','Korea, Republic Of'),(144,'KW','Kuwait'),(145,'KG','Kyrgyzstan'),(146,'LA','Lao People\'s Democratic Republic'),(147,'LV','Latvia'),(148,'LB','Lebanon'),(149,'LS','Lesotho'),(150,'LR','Liberia'),(151,'LY','Libyan Arab Jamahiriya'),(152,'LI','Liechtenstein'),(153,'LT','Lithuania'),(154,'LU','Luxembourg'),(155,'MO','Macao'),(156,'MK','Macedonia, The Former Yugoslav Republic Of'),(157,'MG','Madagascar'),(158,'MW','Malawi'),(159,'MY','Malaysia'),(160,'MV','Maldives'),(161,'ML','Mali'),(162,'MT','Malta'),(163,'MH','Marshall Islands'),(164,'MQ','Martinique'),(165,'MR','Mauritania'),(166,'MU','Mauritius'),(167,'YT','Mayotte'),(168,'MX','Mexico'),(169,'FM','Micronesia, Federated States Of'),(170,'MD','Moldova, Republic Of'),(171,'MC','Monaco'),(172,'MN','Mongolia'),(173,'MS','Montserrat'),(174,'MA','Morocco'),(175,'MZ','Mozambique'),(176,'MM','Myanmar'),(177,'NA','Namibia'),(178,'NR','Nauru'),(179,'NP','Nepal'),(180,'NL','Netherlands'),(181,'AN','Netherlands Antilles'),(182,'NC','New Caledonia'),(183,'NZ','New Zealand'),(184,'NI','Nicaragua'),(185,'NE','Niger'),(186,'NG','Nigeria'),(187,'NU','Niue'),(188,'NF','Norfolk Island'),(189,'MP','Northern Mariana Islands'),(190,'NO','Norway'),(191,'OM','Oman'),(192,'PK','Pakistan'),(193,'PW','Palau'),(194,'PS','Palestinian Territory, Occupied'),(195,'PA','Panama'),(196,'PG','Papua New Guinea'),(197,'PY','Paraguay'),(198,'PE','Peru'),(199,'PH','Philippines'),(200,'PN','Pitcairn'),(201,'PL','Poland'),(202,'PT','Portugal'),(203,'PR','Puerto Rico'),(204,'QA','Qatar'),(205,'RE','Reunion'),(206,'RO','Romania'),(207,'RU','Russian Federation'),(208,'RW','Rwanda'),(209,'SH','Saint Helena'),(210,'KN','Saint Kitts and Nevis'),(211,'LC','Saint Lucia'),(212,'PM','Saint Pierre and Miquelon'),(213,'VC','Saint Vincent and The Grenadines'),(214,'WS','Samoa'),(215,'SM','San Marino'),(216,'ST','Sao Tome and Principe'),(217,'SA','Saudi Arabia'),(218,'SN','Senegal'),(219,'CS','Serbia and Montenegro'),(220,'SC','Seychelles'),(221,'SL','Sierra Leone'),(222,'SG','Singapore'),(223,'SK','Slovakia'),(224,'SI','Slovenia'),(225,'SB','Solomon Islands'),(226,'SO','Somalia'),(227,'ZA','South Africa'),(228,'GS','South Georgia and The South Sandwich Islands'),(229,'ES','Spain'),(230,'LK','Sri Lanka'),(231,'SD','Sudan'),(232,'SR','Suriname'),(233,'SJ','Svalbard and Jan Mayen'),(234,'SZ','Swaziland'),(235,'SE','Sweden'),(236,'CH','Switzerland'),(237,'SY','Syrian Arab Republic'),(238,'TW','Taiwan, Province Of China'),(239,'TJ','Tajikistan'),(240,'TZ','Tanzania, United Republic Of'),(241,'TH','Thailand'),(242,'TL','Timor-leste'),(243,'TG','Togo'),(244,'TK','Tokelau'),(245,'TO','Tonga'),(246,'TT','Trinidad and Tobago'),(247,'TN','Tunisia'),(248,'TR','Turkey'),(249,'TM','Turkmenistan'),(250,'TC','Turks and Caicos Islands'),(251,'TV','Tuvalu'),(252,'UG','Uganda'),(253,'UA','Ukraine'),(254,'AE','United Arab Emirates'),(255,'GB','United Kingdom'),(256,'US','United States'),(257,'UM','United States Minor Outlying Islands'),(258,'UY','Uruguay'),(259,'UZ','Uzbekistan'),(260,'VU','Vanuatu'),(261,'VE','Venezuela'),(262,'VN','Vietnam'),(263,'VG','Virgin Islands, British'),(264,'VI','Virgin Islands, U.S.'),(265,'WF','Wallis and Futuna'),(266,'EH','Western Sahara'),(267,'YE','Yemen'),(268,'ZM','Zambia'),(269,'ZW','Zimbabwe');

insert  into `languages`(`ID`,`NAME`,`LANGUAGECODE`) values (1,'Afar','aa'),(2,'Abkhazian','ab'),(3,'Afrikaans','af'),(4,'Amharic','am'),(5,'Arabic','ar'),(6,'Assamese','as'),(7,'Aymara','ay'),(8,'Azerbaijani','az'),(9,'Bashkir','ba'),(10,'Byelorussian','be'),(11,'Bulgarian','bg'),(12,'Bihari','bh'),(13,'Bislama','bi'),(14,'Bengali','bn'),(15,'Tibetan','bo'),(16,'Breton','br'),(17,'Catalan','ca'),(18,'Corsican','co'),(19,'Czech','cs'),(20,'Welsh','cy'),(21,'Danish','da'),(22,'German','de'),(23,'Bhutani','dz'),(24,'Greek','el'),(25,'English','en'),(26,'Esperanto','eo'),(27,'Spanish','es'),(28,'Estonian','et'),(29,'Basque','eu'),(30,'Persian','fa'),(31,'Finnish','fi'),(32,'Fiji','fj'),(33,'Faroese','fo'),(34,'French','fr'),(35,'Frisian','fy'),(36,'Irish','ga'),(37,'Scots Gaelic','gd'),(38,'Galician','gl'),(39,'Guarani','gn'),(40,'Gujarati','gu'),(41,'Hausa','ha'),(42,'Hebrew','he'),(43,'Hindi','hi'),(44,'Croatian','hr'),(45,'Hungarian','hu'),(46,'Armenian','hy'),(47,'Interlingua','ia'),(48,'Indonesian','id'),(49,'Interlingue','ie'),(50,'Inupiak','ik'),(51,'Icelandic','is'),(52,'Italian','it'),(53,'Inuktitut','iu'),(54,'Japanese','ja'),(55,'Javanese','jw'),(56,'Georgian','ka'),(57,'Kazakh','kk'),(58,'Greenlandic','kl'),(59,'Cambodian','km'),(60,'Kannada','kn'),(61,'Korean','ko'),(62,'Kashmiri','ks'),(63,'Kurdish','ku'),(64,'Kirghiz','ky'),(65,'Latin','la'),(66,'Lingala','ln'),(67,'Laothian','lo'),(68,'Lithuanian','lt'),(69,'Lettish','lv'),(70,'Malagasy','mg'),(71,'Maori','mi'),(72,'Macedonian','mk'),(73,'Malayalam','ml'),(74,'Mongolian','mn'),(75,'Moldavian','mo'),(76,'Marathi','mr'),(77,'Malay','ms'),(78,'Maltese','mt'),(79,'Burmese','my'),(80,'Nauru','na'),(81,'Nepali','ne'),(82,'Dutch','nl'),(83,'Norwegian','no'),(84,'Occitan','oc'),(85,'Oromo','om'),(86,'Oriya','or'),(87,'Punjabi','pa'),(88,'Polish','pl'),(89,'Pashto','ps'),(90,'Portuguese','pt'),(91,'Quechua','qu'),(92,'Rhaeto-Romance','rm'),(93,'Kirundi','rn'),(94,'Romanian','ro'),(95,'Russian','ru'),(96,'Kinyarwanda','rw'),(97,'Sanskrit','sa'),(98,'Sindhi','sd'),(99,'Sangho','sg'),(100,'Serbo-Croatian','sh'),(101,'Sinhalese','si'),(102,'Slovak','sk'),(103,'Slovenian','sl'),(104,'Samoan','sm'),(105,'Shona','sn'),(106,'Somali','so'),(107,'Albanian','sq'),(108,'Serbian','sr'),(109,'Siswati','ss'),(110,'Sesotho','st'),(111,'Sundanese','su'),(112,'Swedish','sv'),(113,'Swahili','sw'),(114,'Tamil','ta'),(115,'Telugu','te'),(116,'Tajik','tg'),(117,'Thai','th'),(118,'Tigrinya','ti'),(119,'Turkmen','tk'),(120,'Tagalog','tl'),(121,'Setswana','tn'),(122,'Tonga','to'),(123,'Turkish','tr'),(124,'Tsonga','ts'),(125,'Tatar','tt'),(126,'Twi','tw'),(127,'Uighur','ug'),(128,'Ukrainian','uk'),(129,'Urdu','ur'),(130,'Uzbek','uz'),(131,'Vietnamese','vi'),(132,'Volapuk','vo'),(133,'Wolof','wo'),(134,'Xhosa','xh'),(135,'Yiddish','yi'),(136,'Yoruba','yo'),(137,'Zhuang','za'),(138,'Chinese','zh'),(139,'Zulu','zu');

insert  into `industrialsector`(`ID`,`SECTOR`) values (1,'Agro-industry'),(2,'Petrolium, Petrochemical and Plastic'),(3,'Electrical and Electronic'),(4,'Chemical (Industrial & Agro)'),(5,'Heavy Industry (Cemont, Iron, Steel, Aluminium)'),(6,'Utilities & Services (Electricity, Water supply)'),(7,'General Consumer Goods'),(8,'Waste Management'),(9,'Others');

INSERT INTO `usergroup` VALUES (2,'Tools'),(102,'Admin');
INSERT INTO `usergroup_roles` VALUES (2,'READ_ROLE'),(2,'EXPORT_ROLE'),(2,'CHECKOUT_ROLE'),(2,'CHECKIN_ROLE'),(2,'RELEASE_ROLE'),(2,'DELETE_ROLE'),(102,'READ_ROLE'),(102,'EXPORT_ROLE'),(102,'CHECKOUT_ROLE'),(102,'CHECKIN_ROLE'),(102,'RELEASE_ROLE'),(102,'DELETE_ROLE'),(102,'MANAGE_USER_ROLE'),(102,'ADMIN_ROLE');

-- insert admin user (password is: "default")
INSERT INTO `user`(`ID`,`USERNAME`,`PASSWORD_HASH`,`PASSWORD_HASH_SALT`) VALUES (1,'admin','8800cc078f01fe328c838ef40245671ae9ddb41ecefc2cff9b25546d0de77d18bdefeb4cdac5e120e094b9b0c9b0439be4166cbd1924d7f9c9ddf2c4b9959473','wqugu4btwsqau!6bb6ie');
INSERT INTO `usergroup_user` VALUES (102,1);
