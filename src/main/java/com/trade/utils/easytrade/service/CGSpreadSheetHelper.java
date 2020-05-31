package com.trade.utils.easytrade.service;

import com.trade.utils.easytrade.model.report.TransactionRecord;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.util.List;
import java.util.Map;

public interface CGSpreadSheetHelper {

    void setMappedTransactions(Map<TransactionRecord, List<TransactionRecord>> mappedTransactions);

    void setWorkbook(SXSSFWorkbook workbook);

    /** create spread with given name and mapped transactions set before */
    void createSpreadSheet(String name);
}
