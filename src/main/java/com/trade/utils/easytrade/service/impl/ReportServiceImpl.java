package com.trade.utils.easytrade.service.impl;

import com.trade.utils.easytrade.dao.ReportDao;
import com.trade.utils.easytrade.model.report.ReportDetails;
import com.trade.utils.easytrade.model.report.TransactionRecord;
import com.trade.utils.easytrade.service.ExcelReportHelper;
import com.trade.utils.easytrade.service.ReportService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Inject
    private ReportDao reportDao;

    @Inject
    private ExcelReportHelper excelReportHelper;

    @Override
    public void generateMappedTransactionsReport(LocalDate startDate, LocalDate endDate) {
        Map<TransactionRecord, List<TransactionRecord>> mappedTransactions = reportDao
                .getMappedTransactions(startDate, endDate);

        ReportDetails reportDetails = buildReportDetails(startDate, endDate, mappedTransactions);

        excelReportHelper.generateMappedTransactionsReport(reportDetails);
    }

    private ReportDetails buildReportDetails(LocalDate startDate, LocalDate endDate,
                                             Map<TransactionRecord, List<TransactionRecord>> mappedTransactions) {
        Map<TransactionRecord, List<TransactionRecord>> stCGMappedTransactions = new LinkedHashMap<>();
        Map<TransactionRecord, List<TransactionRecord>> ltCGMappedTransactions = new LinkedHashMap<>();
        Map<TransactionRecord, List<TransactionRecord>> speculativeMappedTransactions = new LinkedHashMap<>();

        for (Map.Entry<TransactionRecord, List<TransactionRecord>> mappedTransaction : mappedTransactions.entrySet()) {
            TransactionRecord sellTransaction = mappedTransaction.getKey();

            TransactionRecord speculativeSellTransaction = copyWithoutQuantity(sellTransaction);
            TransactionRecord shortTermSellTransaction = copyWithoutQuantity(sellTransaction);
            TransactionRecord longTermSellTransaction = copyWithoutQuantity(sellTransaction);

            LocalDate sellTransactionDate = sellTransaction.getTransactionDate();
            for (TransactionRecord buyTransaction: mappedTransaction.getValue()) {
                LocalDate buyTransactionDate = buyTransaction.getTransactionDate();

                if (isSpeculativeTransactions(sellTransactionDate, buyTransactionDate)) {
                    speculativeSellTransaction.addQuantity(buyTransaction.getQuantity());
                    speculativeMappedTransactions
                            .computeIfAbsent(sellTransaction, k -> new ArrayList<>())
                            .add(buyTransaction);
                } else if (isShortTermTransaction(sellTransactionDate, buyTransactionDate)) {
                    shortTermSellTransaction.addQuantity(buyTransaction.getQuantity());
                    stCGMappedTransactions
                            .computeIfAbsent(sellTransaction, k -> new ArrayList<>())
                            .add(buyTransaction);
                } else {
                    // long term
                    longTermSellTransaction.addQuantity(buyTransaction.getQuantity());
                    ltCGMappedTransactions
                            .computeIfAbsent(sellTransaction, k -> new ArrayList<>())
                            .add(buyTransaction);
                }
            }

        }

        return ReportDetails.builder()
                .startDate(startDate)
                .endDate(endDate)
                .mappedTransactions(mappedTransactions)
                .speculativeMappedTransactions(speculativeMappedTransactions)
                .stCGMappedTransactions(stCGMappedTransactions)
                .ltCGMappedTransactions(ltCGMappedTransactions)
                .build();
    }

    private TransactionRecord copyWithoutQuantity(TransactionRecord sellTransaction) {
        return sellTransaction.toBuilder().quantity(0).build();
    }

    private boolean isSpeculativeTransactions(LocalDate sellTransactionDate, LocalDate buyTransactionDate) {
        return sellTransactionDate.isEqual(buyTransactionDate);
    }

    private boolean isShortTermTransaction(LocalDate sellTransactionDate, LocalDate buyTransactionDate) {
        return Period
                .between(buyTransactionDate, sellTransactionDate)
                .getYears() == 0;
    }
}
