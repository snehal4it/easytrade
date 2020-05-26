-- match transaction records in batch
CREATE OR REPLACE FUNCTION match_transaction_record_batch(batch_size integer) RETURNS VOID
LANGUAGE plpgsql
AS $$
DECLARE
    mapped_scrip_name CHARACTER VARYING;
    buy_trans_found BOOLEAN;
    pending_sell_quantity sell_transaction.quantity%TYPE;
    split_trans_id1 buy_transaction.id%TYPE;
    split_trans_id2 buy_transaction.id%TYPE;

    pending_trans_cur CURSOR FOR
        select * from sell_transaction
        where status = 110
        order by transaction_date, scrip_name, id
        limit batch_size;

    -- compare by mapped (latest) name as scrip name may have changed between buy and sell
    matching_buy_trans_cur CURSOR (sell_trans_scrip CHARACTER VARYING) FOR
        select b.*
        from buy_transaction b join scrip_mapping sm
            on b.scrip_name = sm.original_name
        where status = 110
        and sm.mapped_name = sell_trans_scrip
        order by b.transaction_date, b.id;
BEGIN
    FOR sell_trans in pending_trans_cur LOOP

        -- get latest mapped name
        SELECT mapped_name into mapped_scrip_name FROM scrip_mapping WHERE original_name = sell_trans.scrip_name;

        -- get matching buy transaction
        buy_trans_found := false;
        pending_sell_quantity := sell_trans.quantity;
        FOR buy_trans in matching_buy_trans_cur(mapped_scrip_name) LOOP
            buy_trans_found := true;

            -- case when quantity is matching or less than sell quantity
            IF buy_trans.quantity <= pending_sell_quantity THEN
                -- mark buy transaction as matched
                UPDATE buy_transaction set status = 120, sell_transaction_id = sell_trans.id where id = buy_trans.id;

                pending_sell_quantity := pending_sell_quantity - buy_trans.quantity;
            ELSE
                -- case when buy transaction quantity is more than sell quantity
                -- split buy transaction record
                -- record that matches quantity of sell transaction
                INSERT INTO buy_transaction (transaction_date, scrip_name, old_mapped_name,
                    quantity, market_price, transaction_price, status, sell_transaction_id)
                VALUES (buy_trans.transaction_date, buy_trans.scrip_name, buy_trans.old_mapped_name,
                    pending_sell_quantity, buy_trans.market_price, buy_trans.transaction_price, 120, sell_trans.id)
                RETURNING id into split_trans_id1;

                -- additional quantity is marked as pending
                INSERT INTO buy_transaction (transaction_date, scrip_name, old_mapped_name,
                    quantity, market_price, transaction_price, status)
                VALUES (buy_trans.transaction_date, buy_trans.scrip_name, buy_trans.old_mapped_name,
                    (buy_trans.quantity - pending_sell_quantity), buy_trans.market_price, buy_trans.transaction_price,
                    buy_trans.status)
                RETURNING id into split_trans_id2;

                -- mark existing record as split
                UPDATE buy_transaction set status = 60 where id = buy_trans.id;
                INSERT INTO buy_transaction_link (source_id, target_id)
                VALUES (buy_trans.id, split_trans_id1),
                (buy_trans.id, split_trans_id2);

                pending_sell_quantity := 0;
            END IF;

            -- if pending sell quantity is zero, mark sell transaction as processed and exit
            IF pending_sell_quantity = 0 THEN
                UPDATE sell_transaction SET status = 120 WHERE id = sell_trans.id;
                EXIT;
            END IF;
        END LOOP;

        -- if matching buy transaction not found then throw error
        IF buy_trans_found = false OR pending_sell_quantity != 0 THEN
            RAISE EXCEPTION 'Matching Buy Transaction Not Found for--> date=% scrip=% quantity=%, pending quantity=%',
                sell_trans.transaction_date, sell_trans.scrip_name, sell_trans.quantity, pending_sell_quantity
            USING HINT = 'Ignore or Add missing buy transactions';
        END IF;


    END LOOP;
END;
$$;
GRANT EXECUTE ON FUNCTION match_transaction_record_batch TO easytrade_app;


CREATE OR REPLACE FUNCTION match_transaction_record() RETURNS INTEGER
LANGUAGE plpgsql
AS $$
DECLARE
    pending_transaction_count numeric;
    num_of_iter integer;
    batch_size integer;
BEGIN

    SELECT cast(value as integer) FROM config into batch_size WHERE name = 'MATCH_TRANSACTION_BATCH_SIZE';
    SELECT cast(count(*) as numeric) INTO pending_transaction_count FROM sell_transaction WHERE status = 110;

    num_of_iter := ceil(pending_transaction_count / batch_size);
    -- process in for loop
    -- postgres 10 does not support autonomous transaction, as of now rely on caller to commit transaction
    FOR i in 1..num_of_iter LOOP
        PERFORM match_transaction_record_batch(batch_size);
    END LOOP;

    return 0;
END;
$$;
GRANT EXECUTE ON FUNCTION match_transaction_record TO easytrade_app;