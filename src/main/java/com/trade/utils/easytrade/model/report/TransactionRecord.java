package com.trade.utils.easytrade.model.report;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Builder
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TransactionRecord {
    @EqualsAndHashCode.Include
    private long id;

    private LocalDate transactionDate;

    private String scripName;

    private int quantity;

    private double unitPrice;
}
