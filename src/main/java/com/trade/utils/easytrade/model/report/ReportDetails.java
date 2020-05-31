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
    private Map<TransactionRecord, List<TransactionRecord>> mappedTransactions;
}
