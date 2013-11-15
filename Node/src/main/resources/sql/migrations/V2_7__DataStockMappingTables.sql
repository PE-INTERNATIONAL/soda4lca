-- --------------------
-- data stock mapping tables (name of DA id column)
-- --------------------

ALTER TABLE `datastock_flow` CHANGE `DataStock_ID` `containingDataStocks_ID` BIGINT( 20 ) NOT NULL;
ALTER TABLE `datastock_flowProperty` CHANGE `DataStock_ID` `containingDataStocks_ID` BIGINT( 20 ) NOT NULL;
ALTER TABLE `datastock_contact` CHANGE `DataStock_ID` `containingDataStocks_ID` BIGINT( 20 ) NOT NULL;
ALTER TABLE `datastock_source` CHANGE `DataStock_ID` `containingDataStocks_ID` BIGINT( 20 ) NOT NULL;
ALTER TABLE `datastock_lciamethod` CHANGE `DataStock_ID` `containingDataStocks_ID` BIGINT( 20 ) NOT NULL;
ALTER TABLE `datastock_unitgroup` CHANGE `DataStock_ID` `containingDataStocks_ID` BIGINT( 20 ) NOT NULL;