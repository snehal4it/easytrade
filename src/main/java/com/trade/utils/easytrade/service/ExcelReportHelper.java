package com.trade.utils.easytrade.service;

import com.trade.utils.easytrade.model.report.TransactionRecord;

import java.util.List;
import java.util.Map;

public interface ExcelReportHelper {
    /**
     * Generate excel file with input details at predefined location
     * @param mappedTransactions mapped transaction details
     */
    void generateMappedTransactionsReport(Map<TransactionRecord, List<TransactionRecord>> mappedTransactions);
}
