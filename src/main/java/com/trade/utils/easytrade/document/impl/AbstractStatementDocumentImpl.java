package com.trade.utils.easytrade.document.impl;

import com.trade.utils.easytrade.document.StatementDocument;
import com.trade.utils.easytrade.model.Transaction;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * common statement document operations
 */
public abstract class AbstractStatementDocumentImpl implements StatementDocument {

    private static final DateTimeFormatter TRANSACTION_DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");

    /** some record doesn't have time associated with it */
    private static final DateTimeFormatter TRANSACTION_DATE_FORMATTER
            = DateTimeFormatter.ofPattern("dd MMM yyyy");

    /**
     * @return table tag that contains headers and transactions
     */
    public abstract Element getStatementDetailsContainer();

    @Override
    public List<String> getHeaders() {
        Element tableElement = getStatementDetailsContainer();
        // table -> tbody -> tr
        Element headerElement = tableElement.child(0).child(0);
        return headerElement.children().stream()
                .map(header -> header.text()).collect(Collectors.toList());
    }

    @Override
    public List<Transaction> getTransactions() {
        Element tableElement = getStatementDetailsContainer();
        // table -> tbody -> tr(s)
        Elements tableDetails = tableElement.child(0).children();

        List<Transaction> transactions = new ArrayList<>(tableDetails.size() - 1);
        // skip first header row
        for (int i = 1; i < tableDetails.size(); i++) {
            Transaction transaction = buildTransaction(tableDetails.get(i));
            transactions.add(transaction);
        }

        return transactions;
    }

    private LocalDateTime getTransactionDateTime(String transactionDateTimeStr) {
        try {
            return LocalDateTime.parse(transactionDateTimeStr, TRANSACTION_DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            // some records are without time
            return LocalDate.parse(transactionDateTimeStr, TRANSACTION_DATE_FORMATTER)
                    .atStartOfDay();
        }
    }

    /**
     * Parse row details and creates transaction
     * @param transactionRow transaction row details
     * @return transaction
     */
    private Transaction buildTransaction(Element transactionRow) {
        String transactionDateTimeStr = transactionRow.child(0).text();
        LocalDateTime transactionDateTime = getTransactionDateTime(transactionDateTimeStr);

        String scripCode = transactionRow.child(1).text();
        String scripName = transactionRow.child(2).text();
        String series = transactionRow.child(3).text();
        String exchangeCode = transactionRow.child(4).text();
        String bookType = transactionRow.child(5).text();
        String settlementNumber = transactionRow.child(6).text();
        String orderNumber =  transactionRow.child(7).text();
        String tradeNumber =  transactionRow.child(8).text();
        String transactionType =  transactionRow.child(9).text();
        int quantity = Double.valueOf(transactionRow.child(10).text()).intValue();
        double marketPrice = Double.parseDouble(transactionRow.child(11).text());
        double transactionPrice = Double.parseDouble(transactionRow.child(12).text());
        double totalAmount = Double.parseDouble(transactionRow.child(13).text());

        return Transaction.builder()
                .transactionDateTime(transactionDateTime)
                .scripCode(scripCode)
                .scripName(scripName)
                .series(series)
                .exchangeCode(exchangeCode)
                .bookType(bookType)
                .settlementNumber(settlementNumber)
                .orderNumber(orderNumber)
                .tradeNumber(tradeNumber)
                .transactionType(transactionType)
                .quantity(quantity)
                .marketPrice(marketPrice)
                .transactionPrice(transactionPrice)
                .totalAmount(totalAmount)
                .build();
    }
}
