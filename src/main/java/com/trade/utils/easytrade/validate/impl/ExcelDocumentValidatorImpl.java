package com.trade.utils.easytrade.validate.impl;

import com.trade.utils.easytrade.document.StatementDocument;
import com.trade.utils.easytrade.exception.ValidationException;
import com.trade.utils.easytrade.validate.StatementDocumentValidator;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

import static com.trade.utils.easytrade.util.Constant.EXCEL_DOCUMENT_HEADERS;

@Named
public class ExcelDocumentValidatorImpl implements StatementDocumentValidator {

    @Inject
    private TransactionValidatorImpl transactionValidator;

    @Override
    public void validate(StatementDocument statementDocument) {
        if (statementDocument == null) {
            throw new ValidationException("statementDocument is null");
        }

        List<String> headers = statementDocument.getHeaders();
        if (!EXCEL_DOCUMENT_HEADERS.equals(headers)) {
            String errorMessage = String.format(
                    "excel document headers are different, expected %s, actual %s",
                    EXCEL_DOCUMENT_HEADERS, headers);
            throw new ValidationException(errorMessage);
        }

        transactionValidator.validateTransactions(statementDocument.getTransactions());
    }
}
