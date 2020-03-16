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


