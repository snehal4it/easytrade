package com.trade.utils.easytrade.model.report;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Builder
@Data
public class ReportDetails {
    private LocalDate startDate;
    private LocalDate endDate;
    /** all mapped transactions between given date range */
    private Map<TransactionRecord, List<TransactionRecord>> mappedTransactions;

    /** mapped transactions that fall into short term capital gain */
    private Map<TransactionRecord, List<TransactionRecord>> stCGMappedTransactions;

    /** mapped transactions that fall into long term capital gain */
    private Map<TransactionRecord, List<TransactionRecord>> ltCGMappedTransactions;

    /** mapped transactions that fall into speculative capital gain */
    private Map<TransactionRecord, List<TransactionRecord>> speculativeMappedTransactions;
}
