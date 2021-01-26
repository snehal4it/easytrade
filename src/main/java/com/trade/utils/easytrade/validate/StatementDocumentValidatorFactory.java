package com.trade.utils.easytrade.validate;

import com.trade.utils.easytrade.document.StatementDocument;

/**
 * Creates validators based on input document
 */
public interface StatementDocumentValidatorFactory {

    /**
     * create validator for given statement document
     * @param statementDocument statement document
     * @return validator instance
     */
    StatementDocumentValidator create(StatementDocument statementDocument);
}
