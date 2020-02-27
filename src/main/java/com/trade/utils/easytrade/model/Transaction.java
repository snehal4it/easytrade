package com.trade.utils.easytrade.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class Transaction {
    private LocalDateTime transactionDateTime;

    private String scripCode;

    private String scripName;

    private String series;

    private String exchangeCode;

    private String bookType;

    private String settlementNumber;

    private String orderNumber;

    private String tradeNumber;

    private String transactionType;

    private int quantity;

    private double marketPrice;

    private double transactionPrice;

    private double totalAmount;
}
