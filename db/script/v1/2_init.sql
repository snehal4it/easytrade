REVOKE ALL ON SCHEMA public FROM public;
GRANT USAGE ON SCHEMA public TO easytrade_app;

CREATE SEQUENCE statement_upload_history_id_seq;
GRANT USAGE, SELECT ON statement_upload_history_id_seq TO easytrade_app;

CREATE TABLE statement_upload_history (
  id BIGINT NOT NULL DEFAULT NEXTVAL('statement_upload_history_id_seq'),
  filename CHARACTER VARYING(256) NOT NULL,
  transaction_count BIGINT NOT NULL,
  created_ts TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  CONSTRAINT statement_upload_history_pk PRIMARY KEY(id),
  CONSTRAINT statement_filename_uq UNIQUE(filename)
);
GRANT SELECT, INSERT ON statement_upload_history TO easytrade_app;

CREATE TABLE transaction_status (
    id NUMERIC(3) NOT NULL,
    status CHARACTER VARYING(16) NOT NULL,
    description CHARACTER VARYING(256),
    CONSTRAINT transaction_status_pk PRIMARY KEY(id),
    CONSTRAINT transaction_status_uq UNIQUE(status)
);
GRANT SELECT ON transaction_status TO easytrade_app;

CREATE SEQUENCE transaction_audit_id_seq;
GRANT USAGE, SELECT ON transaction_audit_id_seq TO easytrade_app;

CREATE TABLE transaction_audit (
  id BIGINT NOT NULL DEFAULT NEXTVAL('transaction_audit_id_seq'),
  statement_upload_history_id BIGINT DEFAULT NULL,
  transaction_date_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  scrip_code CHARACTER VARYING(128),
  scrip_name CHARACTER VARYING(256) NOT NULL,
  series CHARACTER VARYING(8),
  exchange_code CHARACTER VARYING(8),
  book_type CHARACTER VARYING(16),
  settlement_number CHARACTER VARYING(16) NOT NULL,
  order_number CHARACTER VARYING(48) NOT NULL,
  trade_number CHARACTER VARYING(16) NOT NULL,
  transaction_type CHARACTER VARYING(4) NOT NULL,
  quantity INTEGER NOT NULL,
  market_price NUMERIC(10,2) NOT NULL,
  transaction_price NUMERIC(10,2) NOT NULL,
  total_amount NUMERIC(15,2) NOT NULL,
  note CHARACTER VARYING(512) DEFAULT NULL,
  status NUMERIC(3) NOT NULL DEFAULT 10,
  created_ts TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  updated_ts TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  CONSTRAINT transaction_pk PRIMARY KEY(id),
  CONSTRAINT transaction_stmt_upload_hist_fk FOREIGN KEY(statement_upload_history_id)
    REFERENCES statement_upload_history(id),
  CONSTRAINT transaction_status_id_fk FOREIGN KEY(status)
    REFERENCES transaction_status(id),
  CONSTRAINT transaction_record_uq UNIQUE(transaction_date_time, scrip_name,
    settlement_number, order_number, trade_number, transaction_type, quantity, market_price)
);
GRANT SELECT, INSERT, UPDATE ON transaction_audit TO easytrade_app;

CREATE TABLE scrip_mapping (
    original_name CHARACTER VARYING(256) NOT NULL,
    mapped_name CHARACTER VARYING(256) NOT NULL,
    CONSTRAINT scrip_mapping_uk PRIMARY KEY(original_name)
);
GRANT SELECT, INSERT, UPDATE ON scrip_mapping TO easytrade_app;

CREATE TABLE config (
    name CHARACTER VARYING(128) NOT NULL,
    value CHARACTER VARYING(128) NOT NULL,
    description CHARACTER VARYING(256)
);
GRANT SELECT ON config TO easytrade_app;

CREATE SEQUENCE sell_transaction_id_seq;
GRANT USAGE, SELECT ON sell_transaction_id_seq TO easytrade_app;

CREATE TABLE sell_transaction (
  id BIGINT NOT NULL DEFAULT NEXTVAL('sell_transaction_id_seq'),
  transaction_audit_id BIGINT,
  transaction_date DATE NOT NULL,
  scrip_name CHARACTER VARYING(256) NOT NULL,
  old_mapped_name CHARACTER VARYING(256) NOT NULL, -- mapped name at the time of entry is created
  quantity INTEGER NOT NULL,
  market_price NUMERIC(10,2) NOT NULL,
  transaction_price NUMERIC(10,2) NOT NULL,
  status NUMERIC(3) NOT NULL DEFAULT 10,
  created_ts TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  updated_ts TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  CONSTRAINT sell_transaction_pk PRIMARY KEY(id),
  CONSTRAINT sell_transaction_scrip_mapping_fk FOREIGN KEY(scrip_name)
    REFERENCES scrip_mapping(original_name),
  CONSTRAINT sell_transaction_audit_fk FOREIGN KEY(transaction_audit_id)
    REFERENCES transaction_audit(id),
  CONSTRAINT sell_transaction_status_fk FOREIGN KEY(status)
    REFERENCES transaction_status(id)
);
GRANT SELECT, INSERT, UPDATE ON sell_transaction TO easytrade_app;

CREATE TABLE sell_transaction_link (
  source_id BIGINT NOT NULL,
  target_id BIGINT NOT NULL,
  created_ts TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  CONSTRAINT sell_transaction_link_pk PRIMARY KEY(source_id, target_id),
  CONSTRAINT sell_transaction_link_source_fk FOREIGN KEY(source_id)
    REFERENCES sell_transaction(id),
  CONSTRAINT sell_transaction_link_target_fk FOREIGN KEY(target_id)
    REFERENCES sell_transaction(id)
);
GRANT SELECT, INSERT ON sell_transaction_link TO easytrade_app;

CREATE SEQUENCE buy_transaction_id_seq;
GRANT USAGE, SELECT ON buy_transaction_id_seq TO easytrade_app;

CREATE TABLE buy_transaction (
  id BIGINT NOT NULL DEFAULT NEXTVAL('buy_transaction_id_seq'),
  transaction_audit_id BIGINT,
  transaction_date TIMESTAMP WITHOUT TIME ZONE,
  scrip_name CHARACTER VARYING(256) NOT NULL,
  old_mapped_name CHARACTER VARYING(256) NOT NULL, -- mapped name at the time of entry is created
  quantity INTEGER NOT NULL,
  market_price NUMERIC(10,2) NOT NULL,
  transaction_price NUMERIC(10,2) NOT NULL,
  status NUMERIC(3) NOT NULL DEFAULT 10,
  sell_transaction_id BIGINT,
  created_ts TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  updated_ts TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  CONSTRAINT buy_transaction_pk PRIMARY KEY(id),
  CONSTRAINT buy_transaction_scrip_mapping_fk FOREIGN KEY(scrip_name)
    REFERENCES scrip_mapping(original_name),
  CONSTRAINT buy_transaction_audit_fk FOREIGN KEY(transaction_audit_id)
    REFERENCES transaction_audit(id),
  CONSTRAINT buy_transaction_status_fk FOREIGN KEY(status)
    REFERENCES transaction_status(id),
  CONSTRAINT buy_transaction_mapping_fk FOREIGN KEY(sell_transaction_id)
    REFERENCES sell_transaction(id)
);
GRANT SELECT, INSERT, UPDATE ON buy_transaction TO easytrade_app;

CREATE TABLE buy_transaction_link (
  source_id BIGINT NOT NULL,
  target_id BIGINT NOT NULL,
  created_ts TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  CONSTRAINT buy_transaction_link_pk PRIMARY KEY(source_id, target_id),
  CONSTRAINT buy_transaction_link_source_fk FOREIGN KEY(source_id)
    REFERENCES buy_transaction(id),
  CONSTRAINT buy_transaction_link_target_fk FOREIGN KEY(target_id)
    REFERENCES buy_transaction(id)
);
GRANT SELECT, INSERT ON buy_transaction_link TO easytrade_app;



