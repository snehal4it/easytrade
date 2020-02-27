package com.trade.utils.easytrade.document.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Statement Document Type - V1
 */
public class StatementDocumentV1Impl extends AbstractStatementDocumentImpl {

    private Document statementDocument;

    public StatementDocumentV1Impl(Document statementDocument) {
        this.statementDocument = statementDocument;
    }

    @Override
    public Element getStatementDetailsContainer() {
        // html -> body -> table
        return statementDocument.body().child(0);
    }
}
