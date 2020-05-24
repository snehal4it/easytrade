insert into transaction_status (id, status, description)
values (10, 'PENDING', 'initial status'),
(20, 'SUCCESS', 'transaction is processed successfully'),
(30, 'FAILED', 'error during transaction processing'),
(40, 'IGNORE', 'ignore transaction e.g. no matching (buy) transaction found');

insert into transaction_status (id, status, description)
values (50, 'MERGED', 'transaction record is merged and should be ignored'),
(60, 'SPLIT', 'transaction record is split and should be ignored');

insert into transaction_status (id, status, description)
values (100, 'MERGED_SYNTHETIC', 'synthetic merge for simplified processing'),
(110, 'MATCH_PENDING', 'transaction record is not yet matched'),
(120, 'MATCHED', 'transaction record matched');

insert into config (name, value, description)
values ('TRANSACTION_AUDIT_BATCH_SIZE', '50', 'batch size for processing transaction audit records');