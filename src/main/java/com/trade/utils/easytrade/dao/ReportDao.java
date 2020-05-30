package com.trade.utils.easytrade.dao;

import com.trade.utils.easytrade.model.report.TransactionRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReportDao {

    /**
     * Retrieves sell transactions details between input date range inclusive
     * @param startDate start date (inclusive)
     * @param endDate end date (inclusive)
     * @return sell transactions -> buy transactions mapping between given date range
     */
    Map<TransactionRecord, List<TransactionRecord>> getMappedTransactions(LocalDate startDate, LocalDate endDate);
}
