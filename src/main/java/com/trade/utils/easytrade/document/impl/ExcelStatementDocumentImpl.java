package com.trade.utils.easytrade.document.impl;

import com.trade.utils.easytrade.document.StatementDocument;
import com.trade.utils.easytrade.model.Transaction;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.trade.utils.easytrade.util.DateTimeUtil.getTransactionDateTimeWhenAMPMFormat;
import static org.apache.commons.lang3.StringUtils.trim;

/**
 * Excel document containing transaction details
 */
public class ExcelStatementDocumentImpl implements StatementDocument {

    private static final int HEADER_ROW_NUM = 6;

    private Workbook workbook;

    public ExcelStatementDocumentImpl(Workbook workbook) {
        this.workbook = workbook;
    }

    private Sheet getWorkSheet() {
        return workbook.getSheetAt(0);
    }

    @Override
    public List<String> getHeaders() {
        Sheet sheet = getWorkSheet();
        Row headerRow = sheet.getRow(HEADER_ROW_NUM);

        List<String> headers = new ArrayList<>();
        headerRow.cellIterator().forEachRemaining(cell -> {
            String headerValue = cell.getStringCellValue();
            headers.add(trim(headerValue));
        });

        return headers;
    }

    @Override
    public List<Transaction> getTransactions() {
        Sheet sheet = getWorkSheet();

        List<Transaction> transactions = new ArrayList<>();
        for (int rowNum = HEADER_ROW_NUM + 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Transaction transaction = buildTransaction(sheet.getRow(rowNum));
            if (transaction != null) {
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    private String getStringValue(Row transactionRow, int index) {
        return transactionRow.getCell(index).getStringCellValue();
    }

    private Transaction buildTransaction(Row transactionRow) {
        String status = getStringValue(transactionRow, 9);
        // process only completed order
        if (!"completed".equalsIgnoreCase(status)) {
            return null;
        }

        String transactionDate = getStringValue(transactionRow, 0);
        String transactionTime = getStringValue(transactionRow, 1);

        String transactionDateTimeStr = transactionDate + " " + transactionTime;
        LocalDateTime transactionDateTime = getTransactionDateTimeWhenAMPMFormat(transactionDateTimeStr);

        String scripName = getStringValue(transactionRow, 2);
        String exchangeCode = getStringValue(transactionRow, 3);
        String transactionType =  getStringValue(transactionRow, 4);
        // order price
        double marketPrice = Double.parseDouble(getStringValue(transactionRow, 6));
        // trade price
        double transactionPrice = Double.parseDouble(getStringValue(transactionRow, 7));
        int quantity = Integer.parseInt(getStringValue(transactionRow, 8));
        // Trade value
        double totalAmount = Double.parseDouble(getStringValue(transactionRow, 10));

        String series = getStringValue(transactionRow, 15);

        String tradeNumber =  getStringValue(transactionRow, 18);
        String orderNumber =  getStringValue(transactionRow, 19);


        return Transaction.builder()
                .transactionDateTime(transactionDateTime)
                .scripCode(null)
                .scripName(scripName)
                .series(series)
                .exchangeCode(exchangeCode)
                .bookType(null)
                .settlementNumber("-1")
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
