package com.trade.utils.easytrade.util;

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class Constant {

    /** headers in statement documents */
    public static final List<String> STATEMENT_DOCUMENT_HEADERS = ImmutableList.of(
            "Transaction Date Time",
            "Code",
            "ScripName",
            "Series",
            "ExchangeCode",
            "Book Type",
            "SETTLEMENT NO",
            "ORDER NO",
            "TRADE NO",
            "Buy/Sell",
            "QUANTITY",
            "Market Price",
            "Transactional Price",
            "Amount");

    /** valid transaction types */
    public static final List<String> TRANSACTION_TYPES = ImmutableList.of("B", "S", "BUY", "SELL");

    private Constant() {
    }
}
