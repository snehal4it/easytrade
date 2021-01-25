package com.trade.utils.easytrade.service;

import com.trade.utils.easytrade.model.report.ReportDetails;
import com.trade.utils.easytrade.model.report.TransactionRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Build report with following details:
 * - Short Term Capital Gain
 * - Long Term Capital Gain
 * - Speculative Capital Gain
 */
public interface CGReportBuilder {

    /**
     *  process capital gain
     * @param startDate transaction search start date
     * @param endDate transaction search end date
     * @param mappedTransactions matching sell -> buy transaction details
     * @return report with capital gain details
     */
    ReportDetails buildReportDetails(LocalDate startDate, LocalDate endDate,
                                     Map<TransactionRecord, List<TransactionRecord>> mappedTransactions);
}
