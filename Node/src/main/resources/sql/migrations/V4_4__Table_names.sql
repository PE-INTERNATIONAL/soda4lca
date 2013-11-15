ALTER TABLE REGISTRY RENAME registry;
ALTER TABLE NETWORKNODE RENAME networknode;
ALTER TABLE REGISTRATION_DATA RENAME registration_data;
ALTER TABLE T_NODE_CREDENTIALS RENAME t_node_credentials;
ALTER TABLE T_REGISTRY_CREDENTIALS RENAME t_registry_credentials;
ALTER TABLE T_NONCE RENAME t_nonce;
ALTER TABLE DATASET_REGISTRATION_DATA RENAME dataset_registration_data;
ALTER TABLE DATASET_DEREGISTRATION_REASON RENAME dataset_deregistration_reason;

ALTER TABLE datastock_flowProperty RENAME datastock_flowproperty;

ALTER TABLE contact_description RENAME Contact_DESCRIPTION;
ALTER TABLE contact_name RENAME Contact_NAME;
ALTER TABLE flow_description RENAME Flow_DESCRIPTION;
ALTER TABLE flow_name RENAME Flow_NAME;
ALTER TABLE flowproperty_description RENAME FlowProperty_DESCRIPTION;
ALTER TABLE flowproperty_name RENAME FlowProperty_NAME;
ALTER TABLE lciamethod_description RENAME LCIAMethod_DESCRIPTION;
ALTER TABLE lciamethod_name RENAME LCIAMethod_NAME;
ALTER TABLE process_description RENAME Process_DESCRIPTION;
ALTER TABLE process_name RENAME Process_NAME;
ALTER TABLE source_name RENAME Source_NAME;
ALTER TABLE source_description RENAME Source_DESCRIPTION;
ALTER TABLE unitgroup_description RENAME UnitGroup_DESCRIPTION;
ALTER TABLE unitgroup_name RENAME UnitGroup_NAME;
