package com.trade.utils.easytrade.document;

import com.trade.utils.easytrade.model.Transaction;

import java.util.List;

/**
 * Parse statement file
 */
public interface StatementFileParser {

    /**
     * Parses statement documents
     * @param fileContent content of file
     * @return list of transactions in document
     */
    List<Transaction> parse(String fileContent);
}
