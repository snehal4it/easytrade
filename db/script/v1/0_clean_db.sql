drop function if exists process_buy_transaction;
drop function if exists merge_sell_transaction_by_date;
drop function if exists process_transaction_audit;
drop function if exists process_transaction_audit_batch;

drop table if exists buy_transaction_link;
drop table if exists buy_transaction;
drop table if exists sell_transaction_link;
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
