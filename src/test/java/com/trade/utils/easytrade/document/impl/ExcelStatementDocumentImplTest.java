package com.trade.utils.easytrade.document.impl;

import com.trade.utils.easytrade.model.Transaction;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static com.trade.utils.easytrade.util.Constant.EXCEL_DOCUMENT_HEADERS;
import static org.assertj.core.api.Assertions.assertThat;

public class ExcelStatementDocumentImplTest {
    private static final String TEST_FILE_1 = "./ref/testdata/upload/001_1apr2020_to_31dec2020_Order_History.xls";

    @Test
    public void verifyHeaders() throws IOException {
        Workbook workbook = WorkbookFactory.create(new File(TEST_FILE_1));

        ExcelStatementDocumentImpl document = new ExcelStatementDocumentImpl(workbook);
        List<String> headers = document.getHeaders();

        assertThat(headers).isEqualTo(EXCEL_DOCUMENT_HEADERS);
    }

    @Test
    public void verifyTransactions() throws IOException {
        Workbook workbook = WorkbookFactory.create(new File(TEST_FILE_1));

        ExcelStatementDocumentImpl document = new ExcelStatementDocumentImpl(workbook);
        List<Transaction> transactions = document.getTransactions();

        verifySellRecord(transactions);
        verifyBuyRecord(transactions);
    }

    private void verifyBuyRecord(List<Transaction> transactions) {
        Transaction buyTransaction = transactions.get(7);
        assertThat(buyTransaction.getTransactionDateTime())
                .isEqualTo(LocalDateTime.of(2020, 5, 21, 12, 53, 32));
        assertThat(buyTransaction.getScripName()).isEqualTo("ICICI BANK LT FV2");
        assertThat(buyTransaction.getExchangeCode()).isEqualTo("BSE");
        assertThat(buyTransaction.getTransactionType()).isEqualTo("BUY");
        assertThat(buyTransaction.getMarketPrice()).isEqualTo(289);
        assertThat(buyTransaction.getTransactionPrice()).isEqualTo(290.45);
        assertThat(buyTransaction.getQuantity()).isEqualTo(10);
        assertThat(buyTransaction.getTotalAmount()).isEqualTo(2904.5);
        assertThat(buyTransaction.getSeries()).isEqualTo("EQ");
        assertThat(buyTransaction.getTradeNumber()).isEqualTo("5911700");
        assertThat(buyTransaction.getOrderNumber()).isEqualTo("1590118200034695756");

        assertThat(buyTransaction.getScripCode()).isNullOrEmpty();
        assertThat(buyTransaction.getBookType()).isNullOrEmpty();
        assertThat(buyTransaction.getSettlementNumber()).isEqualTo("-1");
    }

    private void verifySellRecord(List<Transaction> transactions) {
        Transaction sellTransaction = transactions.get(0);
        assertThat(sellTransaction.getTransactionDateTime())
                .isEqualTo(LocalDateTime.of(2020, 12, 7, 10, 9, 15));
        assertThat(sellTransaction.getScripName()).isEqualTo("AMARARAJA BATTERIES");
        assertThat(sellTransaction.getExchangeCode()).isEqualTo("BSE");
        assertThat(sellTransaction.getTransactionType()).isEqualTo("SELL");
        assertThat(sellTransaction.getMarketPrice()).isEqualTo(936.5);
        assertThat(sellTransaction.getTransactionPrice()).isEqualTo(931.82);
        assertThat(sellTransaction.getQuantity()).isEqualTo(-46);
        assertThat(sellTransaction.getTotalAmount()).isEqualTo(-42863.72);
        assertThat(sellTransaction.getSeries()).isEqualTo("EQ");
        assertThat(sellTransaction.getTradeNumber()).isEqualTo("2122200");
        assertThat(sellTransaction.getOrderNumber()).isEqualTo("1607398200042184924");

        assertThat(sellTransaction.getScripCode()).isNullOrEmpty();
        assertThat(sellTransaction.getBookType()).isNullOrEmpty();
        assertThat(sellTransaction.getSettlementNumber()).isEqualTo("-1");
    }
}
