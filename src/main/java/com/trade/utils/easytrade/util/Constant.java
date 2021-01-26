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

    /** headers in excel version of statement document */
    public static final List<String> EXCEL_DOCUMENT_HEADERS = ImmutableList.of(
            "Date",
            "Time",
            "Instrument Name",
            "Exchange",
            "Trade Type",
            "Product",
            "Order Price",
            "Trade Price",
            "Trade Qty",
            "Status",
            "Trade Value",
            "Order Type",
            "Trigger Price",
            "Order Validity",
            "Disc Qty",
            "Series",
            "Instrument",
            "Exchange Order No",
            "Trade No",
            "Order Reference No.");

    /** valid transaction types */
    public static final List<String> TRANSACTION_TYPES = ImmutableList.of("B", "S", "BUY", "SELL");

    private Constant() {
    }
}
