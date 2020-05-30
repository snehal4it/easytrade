CREATE OR REPLACE FUNCTION process_buy_transaction() RETURNS VOID
LANGUAGE plpgsql
AS $$
BEGIN
    -- don't merge buy transaction, as transaction needs to be matched in FIFO order
    -- so it is possible that for given sell transaction only first buy transaction needs to be matched
    -- if transaction are merged for give day then this will result in average amount being compared vs
    -- actual amount as per FIFO order.
    update buy_transaction set status = 110 where status = 10;
END;
$$;
GRANT EXECUTE ON FUNCTION process_buy_transaction TO easytrade_app;

-- group transactions occurred on same date, preserve original record for back tracking
CREATE OR REPLACE FUNCTION merge_sell_transaction_by_date() RETURNS VOID
LANGUAGE plpgsql
AS $$
DECLARE
    merged_record_id sell_transaction.id%TYPE;

    -- merged record details where for the same date and scrip multiple record exist
    pending_merge_trans_cur CURSOR FOR
        select transaction_date, scrip_name, old_mapped_name, sum(quantity) quantity,
            round(sum(market_price * quantity)/ sum(quantity), 2) unit_market_price,
            round(sum(transaction_price * quantity)/ sum(quantity), 2) unit_transaction_price
        from sell_transaction
        where status = 10
        group by transaction_date, scrip_name, old_mapped_name
        having count(*) > 1;
BEGIN
    -- update record status directly if for given date only one transaction record exist for scrip
    update sell_transaction set status = 110
    where status = 10 and (transaction_date, scrip_name) in (
        select transaction_date, scrip_name
        from sell_transaction
        where status = 10
        group by transaction_date, scrip_name
        having count(*) = 1);

    -- handle case when multiple records for the same date and scrip exist
    FOR transaction_details in pending_merge_trans_cur LOOP
        -- create merged record
        INSERT INTO sell_transaction (transaction_date, scrip_name, old_mapped_name,
            quantity, market_price, transaction_price, status)
        VALUES (transaction_details.transaction_date, transaction_details.scrip_name,
                transaction_details.old_mapped_name, transaction_details.quantity,
            transaction_details.unit_market_price, transaction_details.unit_transaction_price, 110)
        RETURNING id into merged_record_id;

        -- create link
        insert into sell_transaction_link (source_id, target_id)
        select id, merged_record_id
        from sell_transaction
        where status = 10
        and transaction_date = transaction_details.transaction_date
        and scrip_name = transaction_details.scrip_name;

        -- update existing records as merged
        update sell_transaction set status = 150
        where status = 10
        and transaction_date = transaction_details.transaction_date
        and scrip_name = transaction_details.scrip_name;

    END LOOP;
END;
$$;
GRANT EXECUTE ON FUNCTION merge_sell_transaction_by_date TO easytrade_app;