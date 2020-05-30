package com.trade.utils.easytrade.service;

import java.time.LocalDate;

public interface ReportService {
    /**
     * Generates mapped transaction report for given date range (inclusive) at predefined location
     * @param startDate start date (inclusive)
     * @param endDate end date (inclusive)
     */
    void generateMappedTransactionsReport(LocalDate startDate, LocalDate endDate);
}
