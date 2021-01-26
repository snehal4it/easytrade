package com.trade.utils.easytrade.document.impl;

import com.trade.utils.easytrade.document.DocumentVersionFactory;
import com.trade.utils.easytrade.document.StatementDocument;
import com.trade.utils.easytrade.document.StatementFileParser;
import com.trade.utils.easytrade.model.StatementFile;
import org.apache.poi.ss.usermodel.Workbook;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.InputStream;

@Component
public class DocumentVersionFactoryImpl implements DocumentVersionFactory {

    @Inject
    private StatementFileParser statementFileParser;

    @Override
    public StatementDocument createStatementDocument(Document htmlDocument) {
        Element doc2Element = htmlDocument.body().selectFirst("table tr td b");

        // V2 has additional document headers
        if (doc2Element != null && doc2Element.text().equalsIgnoreCase("RELIGARE BROKING LIMITED")) {
            return new StatementDocumentV2Impl(htmlDocument);
        } else {
            return new StatementDocumentV1Impl(htmlDocument);
        }
    }

    @Override
    public StatementDocument createStatementDocument(Workbook excelDocument) {
        return new ExcelStatementDocumentImpl(excelDocument);
    }

    @Override
    public StatementDocument createStatementDocument(InputStream inputStream) {

        StatementFile statementFile = statementFileParser.parse(inputStream);

        Workbook excelFile = statementFile.getExcelDocument();
        if (excelFile != null) {
            return createStatementDocument(excelFile);
        }

        return createStatementDocument(statementFile.getHtmlDocument());
    }

}
