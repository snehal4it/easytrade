package com.trade.utils.easytrade.document;

import org.apache.poi.ss.usermodel.Workbook;
import org.jsoup.nodes.Document;

import java.io.InputStream;

/**
 * Detects specific version of document and return appropriate implementation to work with specific version
 */
public interface DocumentVersionFactory {

    /** creates version specific statement document from html document */
    StatementDocument createStatementDocument(Document htmlDocument);

    /** creates version specific statement document from excel document */
    StatementDocument createStatementDocument(Workbook excelDocument);

    /** creates version specific statement document from input stream */
    StatementDocument createStatementDocument(InputStream inputStream);
}
