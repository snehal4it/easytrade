package com.trade.utils.easytrade.document.impl;

import com.trade.utils.easytrade.document.StatementFileParser;
import com.trade.utils.easytrade.exception.SystemException;
import com.trade.utils.easytrade.model.StatementFile;
import com.trade.utils.easytrade.util.IOUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.inject.Named;
import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * Parser implementatio that detects different file types containing transactions
 */
@Named
public class StatementFileParserImpl implements StatementFileParser {

    @Override
    public StatementFile parse(InputStream inputStream) {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        Document htmlDocument = null;
        Workbook workbook = createWorkbook(bufferedInputStream);
        if (workbook == null) {
            htmlDocument = createHtmlDocument(bufferedInputStream);
        }

        StatementFile statementFile = new StatementFile()
                .setExcelDocument(workbook)
                .setHtmlDocument(htmlDocument);

        if (!statementFile.isValid()) {
            throw new SystemException("Invalid statement file, expecting html or xls file");
        }

        return statementFile;
    }

    private Document createHtmlDocument(BufferedInputStream bufferedInputStream) {
        Document htmlDocument;
        try {
            String xmlString = IOUtil.getFileContent(bufferedInputStream);
            htmlDocument = Jsoup.parse(xmlString);
        } catch (Exception e) {
            htmlDocument = null;
        }
        return htmlDocument;
    }

    private Workbook createWorkbook(BufferedInputStream bufferedInputStream) {
        Workbook workbook;
        try {
            workbook = WorkbookFactory.create(bufferedInputStream);
        } catch (Exception e) {
            workbook = null;
        }

        return workbook;
    }
}
