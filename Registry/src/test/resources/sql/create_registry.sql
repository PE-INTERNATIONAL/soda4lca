DROP SCHEMA PUBLIC CASCADE;

CREATE TABLE T_USER (
  ID NUMERIC(20) NOT NULL,
  LOGIN VARCHAR(255) NOT NULL,
  PASSWORD VARCHAR(64) NOT NULL,
  EMAIL VARCHAR(255) NOT NULL,
  CONSTRAINT USER_PK PRIMARY KEY(ID)
);

CREATE TABLE T_NODE (
  ID INT GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) not null, 
  NAME varchar(255) NOT NULL,
  DESCRIPTION varchar(255),
  NODEID varchar(255) NOT NULL,
  BASEURL varchar(255) NOT NULL,
  ADMINNAME varchar(255) NOT NULL,
  ADMINEMAILADDRESS varchar(255) NOT NULL,
  ADMINPHONE varchar(255),
  ADMINWEBADDRESS varchar(255),
  STATUS varchar(20) DEFAULT NULL,
  IDENTITY NUMERIC(20) DEFAULT NULL,
  NODE_CREDENTIALS_ID NUMERIC(20),
  REGISTRY_CREDENTIALS_ID NUMERIC(20),
  CONSTRAINT NODE_PK PRIMARY KEY(ID),
  CONSTRAINT UNIQUE_BASEURL UNIQUE(BASEURL),
  CONSTRAINT UNIQUE_NODEID UNIQUE(NODEID)
);

CREATE TABLE T_NODE_CREDENTIALS (
  ID INT GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) not null, 
  ACCESSPASSWORD LONGVARBINARY,
  ACCESSACCOUNT varchar(255) DEFAULT NULL,
  NODEID varchar(255) NOT NULL,
  CONSTRAINT NODE_CRED_PK PRIMARY KEY(ID)
);

CREATE TABLE T_REGISTRY_CREDENTIALS (
  ID INT GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) not null, 
  ACCESSPASSWORD LONGVARBINARY,
  ACCESSACCOUNT varchar(255) DEFAULT NULL,
  CONSTRAINT REG_CRED_PK PRIMARY KEY(ID)
);

CREATE TABLE T_NODE_CHANGE_LOG (
  ID INT GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) not null, 
  NODEID varchar(255) NOT NULL,
  OPERATIONTYPE varchar(20) DEFAULT NULL,
  OPERATIONDATE DATE,
  CONSTRAINT LOG_PK PRIMARY KEY(ID)
);

CREATE TABLE T_NONCE (
  ID INT GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) not null,
  USEDATE DATE,
  VALUE LONGVARBINARY,
  CONSTRAINT NONCE_PK PRIMARY KEY(ID)
);

CREATE TABLE T_DATA_SET (
  ID INT GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) not null,
  UUID varchar(255),
  NAME varchar(255),
  NODE_ID NUMERIC(20),
  OWNER varchar(255),
  USER_ varchar(255),
  USER_EMAIL varchar(255),
  HASH LONGVARBINARY,
  STATUS varchar(20) DEFAULT 'NEW',
  VERSION varchar(255),
  CONSTRAINT DATA_SET_PK PRIMARY KEY(ID)
);

CREATE TABLE T_DATA_SET_COMPLIANCE_UUIDS (
  DS_ID INT,
  UUID varchar(255)
);

CREATE TABLE T_COMPLIANCE (
  ID INT GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) not null,
  NAME varchar(255),
  UUID varchar(255),
  CONSTRAINT T_COMPLIANCE_PK PRIMARY KEY(ID)
) ;


CREATE TABLE T_NODE_AUDIT_LOG (
  ID INT GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) not null,
  NODEID NUMERIC(20),
  OPERATIONTIME NUMERIC(20),
  OPERATIONTYPE varchar(40),
  OBJECTNAME varchar(255),
  CONSTRAINT T_NODE_AUDIT_LOG_PK PRIMARY KEY(ID)
) ;

CREATE TABLE T_DATA_SET_AUDIT_LOG (
  ID INT GENERATED BY DEFAULT AS IDENTITY (START WITH 1 INCREMENT BY 1) not null,
  DATASETID NUMERIC(20),
  UUID varchar(40),
  VERSION varchar(15),
  NODE_AUDIT_LOG_ID NUMERIC(20),
  CONSTRAINT T_DATA_SET_AUDIT_LOG_PK PRIMARY KEY(ID)
) ;
