package com.trade.utils.easytrade.dao;

/**
 * DB operations for upload history
 */
public interface StatementUploadHistoryDao {

    /**
     * Save upload history
     * @param filename file name
     * @param transactionCount number of transactions
     * @return database identifier for upload history
     */
    long save(String filename, int transactionCount);
}
