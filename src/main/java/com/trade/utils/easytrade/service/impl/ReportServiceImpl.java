package com.trade.utils.easytrade.service.impl;

import com.trade.utils.easytrade.dao.ReportDao;
import com.trade.utils.easytrade.model.report.ReportDetails;
import com.trade.utils.easytrade.model.report.TransactionRecord;
import com.trade.utils.easytrade.service.CGReportBuilder;
import com.trade.utils.easytrade.service.ExcelReportHelper;
import com.trade.utils.easytrade.service.ReportService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Generate CG reports
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Inject
    private ReportDao reportDao;

    @Inject
    private CGReportBuilder cgReportBuilder;

    @Inject
    private ExcelReportHelper excelReportHelper;

    @Override
    public void generateMappedTransactionsReport(LocalDate startDate, LocalDate endDate) {
        Map<TransactionRecord, List<TransactionRecord>> mappedTransactions = reportDao
                .getMappedTransactions(startDate, endDate);

        ReportDetails reportDetails = cgReportBuilder.buildReportDetails(startDate, endDate, mappedTransactions);

        excelReportHelper.generateMappedTransactionsReport(reportDetails);
    }


}
