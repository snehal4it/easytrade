package com.trade.utils.easytrade.dao.impl;

import com.trade.utils.easytrade.dao.TransactionDao;
import com.trade.utils.easytrade.model.Transaction;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.List;

@Repository
public class TransactionDaoImpl implements TransactionDao {

    private static final String SAVE_TRANSACTION_QUERY
            = "insert into transaction_audit (statement_upload_history_id, transaction_date_time, scrip_code,"
                + " scrip_name, series, exchange_code, book_type, settlement_number, order_number, trade_number,"
                + " transaction_type, quantity, market_price, transaction_price, total_amount )"
                + " values (:statementUploadHistoryId, :transactionDateTime, :scripCode,"
                + " :scripName, :series, :exchangeCode, :bookType, :settlementNumber, :orderNumber, :tradeNumber,"
                + " :transactionType, :quantity, :marketPrice, :transactionPrice, :totalAmount)";

    @Inject
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void save(long uploadHistoryId, List<Transaction> transactions) {

        SqlParameterSource[] sqlParameterSourceAr = transactions.stream().map(transaction -> {
            SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                    .addValue("statementUploadHistoryId", uploadHistoryId)
                    .addValue("transactionDateTime", transaction.getTransactionDateTime())
                    .addValue("scripCode", transaction.getScripCode())
                    .addValue("scripName", transaction.getScripName())
                    .addValue("series", transaction.getSeries())
                    .addValue("exchangeCode", transaction.getExchangeCode())
                    .addValue("bookType", transaction.getBookType())
                    .addValue("settlementNumber", transaction.getSettlementNumber())
                    .addValue("orderNumber", transaction.getOrderNumber())
                    .addValue("tradeNumber", transaction.getTradeNumber())
                    .addValue("transactionType", transaction.getTransactionType())
                    .addValue("quantity", transaction.getQuantity())
                    .addValue("marketPrice", transaction.getMarketPrice())
                    .addValue("transactionPrice", transaction.getTransactionPrice())
                    .addValue("totalAmount", transaction.getTotalAmount());
            return sqlParameterSource;
        }).toArray(SqlParameterSource[]::new);

        namedParameterJdbcTemplate.batchUpdate(SAVE_TRANSACTION_QUERY, sqlParameterSourceAr);
    }
}
