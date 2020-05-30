-- moves audit records in batches
CREATE OR REPLACE FUNCTION process_transaction_audit_batch(batch_size integer) RETURNS VOID
LANGUAGE plpgsql
AS $$
DECLARE
    scrip_mapping_count integer;
    trans_date date;
    mapped_scrip_name CHARACTER VARYING;

    pending_trans_cur CURSOR FOR
        select * from transaction_audit
        where status = 10
        order by transaction_date_time, id
        limit batch_size;
BEGIN
    FOR transaction_details in pending_trans_cur LOOP
        -- validate scrip mapping
        SELECT count(*) INTO scrip_mapping_count
        FROM scrip_mapping WHERE original_name = transaction_details.scrip_name;

        IF scrip_mapping_count = 0 THEN
            RAISE EXCEPTION 'Scrip Mapping Not Found --> %', transaction_details.scrip_name
                USING HINT = 'Please update script mapping table';
        END IF;

        -- need only date (exclude time)
        trans_date := transaction_details.transaction_date_time::date;
        -- get mapped name at time of creating entry (if scrip name changes multiple time,
        -- preserve name at time of transaction is created)
        SELECT mapped_name into mapped_scrip_name FROM scrip_mapping
            WHERE original_name = transaction_details.scrip_name;

        IF upper(transaction_details.transaction_type) = 'B' OR upper(transaction_details.transaction_type) = 'BUY' THEN
            INSERT INTO buy_transaction (transaction_audit_id, transaction_date, scrip_name,
                old_mapped_name, quantity, market_price, transaction_price)
            VALUES (transaction_details.id, transaction_details.transaction_date_time, transaction_details.scrip_name,
            mapped_scrip_name, transaction_details.quantity, transaction_details.market_price, transaction_details.transaction_price);
        ELSIF upper(transaction_details.transaction_type) = 'S'
            OR upper(transaction_details.transaction_type) = 'SELL' THEN
            INSERT INTO sell_transaction (transaction_audit_id, transaction_date, scrip_name, old_mapped_name,
                quantity, market_price, transaction_price)
            VALUES (transaction_details.id, trans_date, transaction_details.scrip_name, mapped_scrip_name,
                abs(transaction_details.quantity), abs(transaction_details.market_price),
                abs(transaction_details.transaction_price));
        ELSE
            RAISE EXCEPTION 'Invalid Transaction Type --> %', transaction_details.transaction_type
                USING HINT = 'Check Transaction Type';
        END IF;

        -- update audit record as processed
        UPDATE transaction_audit SET status = 20 WHERE transaction_audit.id = transaction_details.id;

    END LOOP;
END;
$$;
GRANT EXECUTE ON FUNCTION process_transaction_audit_batch TO easytrade_app;

-- moves audit records into sell and buy transactions table and mark audit record as processed
CREATE OR REPLACE FUNCTION process_transaction_audit() RETURNS INTEGER
LANGUAGE plpgsql
AS $$
DECLARE
    pending_transaction_count numeric;
    num_of_iter integer;
    batch_size integer;
BEGIN

    SELECT cast(value as integer) FROM config into batch_size WHERE name = 'TRANSACTION_AUDIT_BATCH_SIZE';
    SELECT cast(count(*) as numeric) INTO pending_transaction_count FROM transaction_audit WHERE status = 10;

    num_of_iter := ceil(pending_transaction_count / batch_size);
    -- process in for loop
    -- postgres 10 does not support autonomous transaction, as of now rely on caller to commit transaction
    FOR i in 1..num_of_iter LOOP
        PERFORM process_transaction_audit_batch(batch_size);
    END LOOP;

    return 0;
END;
$$;
GRANT EXECUTE ON FUNCTION process_transaction_audit TO easytrade_app;

