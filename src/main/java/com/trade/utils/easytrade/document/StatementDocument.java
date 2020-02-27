package com.trade.utils.easytrade.document;

import com.trade.utils.easytrade.model.Transaction;

import java.util.List;

/**
 * Generic statement document
 */
public interface StatementDocument {

    /**
     * Retrieves headers from document
     * @return headers
     */
    List<String> getHeaders();

    /**
     * retrieves list of transactions
     * @return transactions
     */
    List<Transaction> getTransactions();
}
