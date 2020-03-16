package com.trade.utils.easytrade.service;

import com.trade.utils.easytrade.document.StatementDocument;

/**
 * Transaction operations
 */
public interface TransactionService {

    /**
     * Save statement document
     * @param filename filename
     * @param statementDocument statement document
     */
    void save(String filename, StatementDocument statementDocument);
}
