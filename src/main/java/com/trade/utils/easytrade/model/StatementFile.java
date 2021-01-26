package com.trade.utils.easytrade.model;

import org.apache.poi.ss.usermodel.Workbook;
import org.jsoup.nodes.Document;

/**
 * Encapsulate multiple file types containing transactions
 */
public class StatementFile {
    /** transactions in html file */
    private Document htmlDocument;

    /** transactions in excel file */
    private Workbook excelDocument;

    public boolean isValid() {
        return (htmlDocument != null || excelDocument != null);
    }

    public Document getHtmlDocument() {
        return htmlDocument;
    }

    public StatementFile setHtmlDocument(Document htmlDocument) {
        this.htmlDocument = htmlDocument;
        return this;
    }

    public Workbook getExcelDocument() {
        return excelDocument;
    }

    public StatementFile setExcelDocument(Workbook excelDocument) {
        this.excelDocument = excelDocument;
        return this;
    }
}
