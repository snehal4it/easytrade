package com.trade.utils.easytrade.document;

import org.jsoup.nodes.Document;

/**
 * Detects specific version of document and return appropriate implementation to work with specific version
 */
public interface DocumentVersionFactory {

    /** creates version specific statement document from html document */
    StatementDocument createStatementDocument(Document htmlDocument);
}
