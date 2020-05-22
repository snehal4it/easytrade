drop table if exists buy_transaction;
drop table if exists sell_transaction;
drop table if exists config;
drop table if exists scrip_mapping;
drop table if exists transaction_audit;
drop table if exists transaction_status;
drop table if exists statement_upload_history;

drop sequence if exists buy_transaction_id_seq;
drop sequence if exists sell_transaction_id_seq;
drop sequence if exists transaction_audit_id_seq;
drop sequence if exists statement_upload_history_id_seq;
