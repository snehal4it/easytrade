package com.trade.utils.easytrade.service.impl;

import com.trade.utils.easytrade.model.report.ReportDetails;
import com.trade.utils.easytrade.model.report.TransactionRecord;
import com.trade.utils.easytrade.service.CGReportBuilder;

import javax.inject.Named;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Build report with following details:
 * - Short Term Capital Gain
 * - Long Term Capital Gain
 * - Speculative Capital Gain
 */
@Named
public class CGReportBuilderImpl implements CGReportBuilder {

    @Override
    public ReportDetails buildReportDetails(LocalDate startDate, LocalDate endDate,
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
                            .computeIfAbsent(speculativeSellTransaction, k -> new ArrayList<>())
                            .add(buyTransaction);
                } else if (isShortTermTransaction(sellTransactionDate, buyTransactionDate)) {
                    shortTermSellTransaction.addQuantity(buyTransaction.getQuantity());
                    stCGMappedTransactions
                            .computeIfAbsent(shortTermSellTransaction, k -> new ArrayList<>())
                            .add(buyTransaction);
                } else {
                    // long term
                    longTermSellTransaction.addQuantity(buyTransaction.getQuantity());
                    ltCGMappedTransactions
                            .computeIfAbsent(longTermSellTransaction, k -> new ArrayList<>())
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
