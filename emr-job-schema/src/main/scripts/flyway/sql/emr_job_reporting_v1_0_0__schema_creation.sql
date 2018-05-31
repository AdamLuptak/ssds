CREATE SEQUENCE emr_job_execution_execution_id_seq;
CREATE SEQUENCE emr_job_messages_message_id_seq;

CREATE TABLE emr_job_execution (
   execution_id INT4 DEFAULT nextval('emr_job_execution_execution_id_seq') NOT NULL,
   cluster_id character varying(40) UNIQUE,
   job_name character varying(40),
   job_status character varying(40),
   sqs_queue_name character varying(40),
   PRIMARY KEY(execution_id)
);

CREATE TABLE emr_job_messages (
 message_id INT4 DEFAULT nextval('emr_job_messages_message_id_seq') NOT NULL,
 execution_id INTEGER,
 message text,
 PRIMARY KEY (message_id, execution_id),
 FOREIGN KEY (execution_id) REFERENCES emr_job_execution (execution_id)
);

CREATE INDEX index_emr_job_execution
ON emr_job_execution (execution_id, cluster_id);

CREATE INDEX index_emr_job_messages
ON emr_job_messages (message_id, execution_id);
