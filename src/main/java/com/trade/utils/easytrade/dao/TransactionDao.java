package com.trade.utils.easytrade.dao;

import com.trade.utils.easytrade.model.Transaction;

import java.util.List;

/**
 * DB operations for Transactions
 */
public interface TransactionDao {

    /**
     * Save transactions
     * @param uploadHistoryId db identifier for upload history
     * @param transactions transactions
     */
    void save(long uploadHistoryId, List<Transaction> transactions);
}
