package com.trade.utils.easytrade.service;

import com.trade.utils.easytrade.model.report.ReportDetails;

public interface ExcelReportHelper {
    /**
     * Generate excel file with input details at predefined location
     * @param reportDetails mapped transaction details
     */
    void generateMappedTransactionsReport(ReportDetails reportDetails);
}
