insert into transaction_status (id, status, description)
values (10, 'PENDING', 'initial status'),
(20, 'SUCCESS', 'transaction is processed successfully'),
(30, 'FAILED', 'error during transaction processing'),
(40, 'IGNORE', 'ignore transaction e.g. no matching (buy) transaction found');