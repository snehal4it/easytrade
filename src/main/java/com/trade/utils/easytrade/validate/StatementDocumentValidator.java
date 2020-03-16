package com.trade.utils.easytrade.validate;

import com.trade.utils.easytrade.document.StatementDocument;

/**
 * Basic statement document validator.
 */
public interface StatementDocumentValidator {

    /**
     * Basic validation of statement document
     * @param statementDocument statement document
     */
    void validate(StatementDocument statementDocument);
}
