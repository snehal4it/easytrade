package com.trade.utils.easytrade.validate.impl;

import com.trade.utils.easytrade.document.StatementDocument;
import com.trade.utils.easytrade.document.impl.ExcelStatementDocumentImpl;
import com.trade.utils.easytrade.validate.StatementDocumentValidator;
import com.trade.utils.easytrade.validate.StatementDocumentValidatorFactory;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class StatementDocumentValidatorFactoryImpl implements StatementDocumentValidatorFactory {

    @Inject
    private ExcelDocumentValidatorImpl excelDocumentValidator;

    @Inject
    private StatementDocumentValidatorImpl statementDocumentValidator;

    @Override
    public StatementDocumentValidator create(StatementDocument statementDocument) {
        if (statementDocument instanceof ExcelStatementDocumentImpl) {
            return excelDocumentValidator;
        } else {
            return statementDocumentValidator;
        }
    }
}
