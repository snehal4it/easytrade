package com.trade.utils.easytrade.document.impl;

import com.trade.utils.easytrade.document.DocumentVersionFactory;
import com.trade.utils.easytrade.document.StatementDocument;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class DocumentVersionFactoryImpl implements DocumentVersionFactory {

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
}
