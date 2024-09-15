-- Connect to the production database and create the wallet schema
\connect production;

CREATE TABLE IF NOT EXISTS crypto_transaction (
                                                  id SERIAL PRIMARY KEY,
                                                  date_utc TIMESTAMP WITH TIME ZONE NOT NULL,
                                                  pair VARCHAR(255) NOT NULL,
                                                  side VARCHAR(10) CHECK (side IN ('BUY', 'SELL')) NOT NULL,
                                                  price DECIMAL(20, 10) NOT NULL,
                                                  executed_amount DECIMAL(20, 10),
                                                  executed_currency VARCHAR(10),
                                                  amount_amount DECIMAL(20, 10),
                                                  amount_currency VARCHAR(10),
                                                  fee_amount DECIMAL(20, 10),
                                                  fee_currency VARCHAR(10)
);

ALTER TABLE crypto_transaction
    ADD CONSTRAINT unique_transaction UNIQUE (date_utc,pair,side,price,executed_amount,executed_currency,amount_amount,amount_currency,fee_amount,fee_currency);

CREATE TABLE IF NOT EXISTS  BATCH_JOB_INSTANCE  (
                                                    JOB_INSTANCE_ID BIGINT  NOT NULL PRIMARY KEY ,
                                                    VERSION BIGINT ,
                                                    JOB_NAME VARCHAR(100) NOT NULL,
                                                    JOB_KEY VARCHAR(32) NOT NULL,
                                                    constraint JOB_INST_UN unique (JOB_NAME, JOB_KEY)
) ;

CREATE TABLE IF NOT EXISTS  BATCH_JOB_EXECUTION  (
                                                     JOB_EXECUTION_ID BIGINT  NOT NULL PRIMARY KEY ,
                                                     VERSION BIGINT  ,
                                                     JOB_INSTANCE_ID BIGINT NOT NULL,
                                                     CREATE_TIME TIMESTAMP NOT NULL,
                                                     START_TIME TIMESTAMP DEFAULT NULL ,
                                                     END_TIME TIMESTAMP DEFAULT NULL ,
                                                     STATUS VARCHAR(10) ,
                                                     EXIT_CODE VARCHAR(2500) ,
                                                     EXIT_MESSAGE VARCHAR(2500) ,
                                                     LAST_UPDATED TIMESTAMP,
                                                     constraint JOB_INST_EXEC_FK foreign key (JOB_INSTANCE_ID)
                                                         references BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
) ;

CREATE TABLE IF NOT EXISTS  BATCH_JOB_EXECUTION_PARAMS  (
                                                            JOB_EXECUTION_ID BIGINT NOT NULL ,
                                                            PARAMETER_NAME VARCHAR(100) NOT NULL ,
                                                            PARAMETER_TYPE VARCHAR(100) NOT NULL ,
                                                            PARAMETER_VALUE VARCHAR(2500) ,
                                                            IDENTIFYING CHAR(1) NOT NULL ,
                                                            constraint JOB_EXEC_PARAMS_FK foreign key (JOB_EXECUTION_ID)
                                                                references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ;

CREATE TABLE IF NOT EXISTS BATCH_STEP_EXECUTION  (
                                                     STEP_EXECUTION_ID BIGINT  NOT NULL PRIMARY KEY ,
                                                     VERSION BIGINT NOT NULL,
                                                     STEP_NAME VARCHAR(100) NOT NULL,
                                                     JOB_EXECUTION_ID BIGINT NOT NULL,
                                                     CREATE_TIME TIMESTAMP NOT NULL,
                                                     START_TIME TIMESTAMP DEFAULT NULL ,
                                                     END_TIME TIMESTAMP DEFAULT NULL ,
                                                     STATUS VARCHAR(10) ,
                                                     COMMIT_COUNT BIGINT ,
                                                     READ_COUNT BIGINT ,
                                                     FILTER_COUNT BIGINT ,
                                                     WRITE_COUNT BIGINT ,
                                                     READ_SKIP_COUNT BIGINT ,
                                                     WRITE_SKIP_COUNT BIGINT ,
                                                     PROCESS_SKIP_COUNT BIGINT ,
                                                     ROLLBACK_COUNT BIGINT ,
                                                     EXIT_CODE VARCHAR(2500) ,
                                                     EXIT_MESSAGE VARCHAR(2500) ,
                                                     LAST_UPDATED TIMESTAMP,
                                                     constraint JOB_EXEC_STEP_FK foreign key (JOB_EXECUTION_ID)
                                                         references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ;

CREATE TABLE IF NOT EXISTS BATCH_STEP_EXECUTION_CONTEXT  (
                                                             STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
                                                             SHORT_CONTEXT VARCHAR(2500) NOT NULL,
                                                             SERIALIZED_CONTEXT TEXT ,
                                                             constraint STEP_EXEC_CTX_FK foreign key (STEP_EXECUTION_ID)
                                                                 references BATCH_STEP_EXECUTION(STEP_EXECUTION_ID)
) ;

CREATE TABLE IF NOT EXISTS BATCH_JOB_EXECUTION_CONTEXT  (
                                                            JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
                                                            SHORT_CONTEXT VARCHAR(2500) NOT NULL,
                                                            SERIALIZED_CONTEXT TEXT ,
                                                            constraint JOB_EXEC_CTX_FK foreign key (JOB_EXECUTION_ID)
                                                                references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ;

CREATE SEQUENCE IF NOT EXISTS BATCH_STEP_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
CREATE SEQUENCE IF NOT EXISTS BATCH_JOB_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
CREATE SEQUENCE IF NOT EXISTS BATCH_JOB_SEQ MAXVALUE 9223372036854775807 NO CYCLE;

-- Connect to the qualification database and create the wallet schema
\connect qualification;

-- Drop the table if it already exists
DROP TABLE IF EXISTS crypto_transaction;

-- Create the table
CREATE TABLE IF NOT EXISTS crypto_transaction (
                                                  id SERIAL PRIMARY KEY,
                                                  date_utc TIMESTAMP WITH TIME ZONE NOT NULL,
                                                  pair VARCHAR(255) NOT NULL,
                                                  side VARCHAR(10) CHECK (side IN ('BUY', 'SELL')) NOT NULL,
                                                  price DECIMAL(20, 10) NOT NULL,
                                                  executed_amount DECIMAL(20, 10),
                                                  executed_currency VARCHAR(10),
                                                  amount_amount DECIMAL(20, 10),
                                                  amount_currency VARCHAR(10),
                                                  fee_amount DECIMAL(20, 10),
                                                  fee_currency VARCHAR(10)
);

ALTER TABLE crypto_transaction
    ADD CONSTRAINT unique_transaction UNIQUE (date_utc,pair,side,price,executed_amount,executed_currency,amount_amount,amount_currency,fee_amount,fee_currency);


CREATE TABLE IF NOT EXISTS  BATCH_JOB_INSTANCE  (
                                                    JOB_INSTANCE_ID BIGINT  NOT NULL PRIMARY KEY ,
                                                    VERSION BIGINT ,
                                                    JOB_NAME VARCHAR(100) NOT NULL,
                                                    JOB_KEY VARCHAR(32) NOT NULL,
                                                    constraint JOB_INST_UN unique (JOB_NAME, JOB_KEY)
) ;

CREATE TABLE IF NOT EXISTS  BATCH_JOB_EXECUTION  (
                                                     JOB_EXECUTION_ID BIGINT  NOT NULL PRIMARY KEY ,
                                                     VERSION BIGINT  ,
                                                     JOB_INSTANCE_ID BIGINT NOT NULL,
                                                     CREATE_TIME TIMESTAMP NOT NULL,
                                                     START_TIME TIMESTAMP DEFAULT NULL ,
                                                     END_TIME TIMESTAMP DEFAULT NULL ,
                                                     STATUS VARCHAR(10) ,
                                                     EXIT_CODE VARCHAR(2500) ,
                                                     EXIT_MESSAGE VARCHAR(2500) ,
                                                     LAST_UPDATED TIMESTAMP,
                                                     constraint JOB_INST_EXEC_FK foreign key (JOB_INSTANCE_ID)
                                                         references BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
) ;

CREATE TABLE IF NOT EXISTS  BATCH_JOB_EXECUTION_PARAMS  (
                                                            JOB_EXECUTION_ID BIGINT NOT NULL ,
                                                            PARAMETER_NAME VARCHAR(100) NOT NULL ,
                                                            PARAMETER_TYPE VARCHAR(100) NOT NULL ,
                                                            PARAMETER_VALUE VARCHAR(2500) ,
                                                            IDENTIFYING CHAR(1) NOT NULL ,
                                                            constraint JOB_EXEC_PARAMS_FK foreign key (JOB_EXECUTION_ID)
                                                                references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ;

CREATE TABLE IF NOT EXISTS BATCH_STEP_EXECUTION  (
                                                     STEP_EXECUTION_ID BIGINT  NOT NULL PRIMARY KEY ,
                                                     VERSION BIGINT NOT NULL,
                                                     STEP_NAME VARCHAR(100) NOT NULL,
                                                     JOB_EXECUTION_ID BIGINT NOT NULL,
                                                     CREATE_TIME TIMESTAMP NOT NULL,
                                                     START_TIME TIMESTAMP DEFAULT NULL ,
                                                     END_TIME TIMESTAMP DEFAULT NULL ,
                                                     STATUS VARCHAR(10) ,
                                                     COMMIT_COUNT BIGINT ,
                                                     READ_COUNT BIGINT ,
                                                     FILTER_COUNT BIGINT ,
                                                     WRITE_COUNT BIGINT ,
                                                     READ_SKIP_COUNT BIGINT ,
                                                     WRITE_SKIP_COUNT BIGINT ,
                                                     PROCESS_SKIP_COUNT BIGINT ,
                                                     ROLLBACK_COUNT BIGINT ,
                                                     EXIT_CODE VARCHAR(2500) ,
                                                     EXIT_MESSAGE VARCHAR(2500) ,
                                                     LAST_UPDATED TIMESTAMP,
                                                     constraint JOB_EXEC_STEP_FK foreign key (JOB_EXECUTION_ID)
                                                         references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ;

CREATE TABLE IF NOT EXISTS BATCH_STEP_EXECUTION_CONTEXT  (
                                                             STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
                                                             SHORT_CONTEXT VARCHAR(2500) NOT NULL,
                                                             SERIALIZED_CONTEXT TEXT ,
                                                             constraint STEP_EXEC_CTX_FK foreign key (STEP_EXECUTION_ID)
                                                                 references BATCH_STEP_EXECUTION(STEP_EXECUTION_ID)
) ;

CREATE TABLE IF NOT EXISTS BATCH_JOB_EXECUTION_CONTEXT  (
                                                            JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
                                                            SHORT_CONTEXT VARCHAR(2500) NOT NULL,
                                                            SERIALIZED_CONTEXT TEXT ,
                                                            constraint JOB_EXEC_CTX_FK foreign key (JOB_EXECUTION_ID)
                                                                references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ;

CREATE SEQUENCE IF NOT EXISTS BATCH_STEP_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
CREATE SEQUENCE IF NOT EXISTS BATCH_JOB_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
CREATE SEQUENCE IF NOT EXISTS BATCH_JOB_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
