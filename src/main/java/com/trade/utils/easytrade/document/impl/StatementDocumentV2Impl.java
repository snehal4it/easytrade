package com.trade.utils.easytrade.document.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Statement Document Type - V2
 */
public class StatementDocumentV2Impl extends AbstractStatementDocumentImpl {

    private Document statementDocument;

    public StatementDocumentV2Impl(Document statementDocument) {
        this.statementDocument = statementDocument;
    }

    @Override
    public Element getStatementDetailsContainer() {
        // html -> body -> table
        return statementDocument.body().child(8);
    }
}
