package com.trade.utils.easytrade.validate.impl;

import com.trade.utils.easytrade.document.StatementDocument;
import com.trade.utils.easytrade.exception.ValidationException;
import com.trade.utils.easytrade.model.Transaction;
import com.trade.utils.easytrade.validate.StatementDocumentValidator;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Named;
import java.util.List;

import static com.trade.utils.easytrade.util.Constant.STATEMENT_DOCUMENT_HEADERS;
import static com.trade.utils.easytrade.util.Constant.TRANSACTION_TYPES;

/**
 * Basic statement document validation implementation.
 */
@Named
public class StatementDocumentValidatorImpl implements StatementDocumentValidator {

    @Override
    public void validate(StatementDocument statementDocument) {
        if (statementDocument == null) {
            throw new ValidationException("statementDocument is null");
        }

        List<String> headers = statementDocument.getHeaders();
        if (!STATEMENT_DOCUMENT_HEADERS.equals(headers)) {
            String errorMessage = String.format(
                    "document headers are different, expected %s, actual %s",
                    STATEMENT_DOCUMENT_HEADERS, headers);
            throw new ValidationException(errorMessage);
        }

        statementDocument.getTransactions().stream().forEach(this::validateTransaction);
    }

    private void validateTransaction(Transaction transaction) {
        if (transaction.getTransactionDateTime() == null) {
            throw new ValidationException("transactionDateTime is required field");
        }

        validateRequiredField(transaction.getScripName(), "scriptName");
        validateRequiredField(transaction.getSettlementNumber(), "settlementNumber");
        validateRequiredField(transaction.getOrderNumber(), "orderNumber");
        validateRequiredField(transaction.getTradeNumber(), "tradeNumber");
        validateRequiredField(transaction.getTransactionType(), "transactionType");

        if (!TRANSACTION_TYPES.contains(transaction.getTransactionType())) {
            throw new ValidationException("invalid transactionType");
        }
    }

    private void validateRequiredField(String value, String fieldName) {
        if (StringUtils.isBlank(value)) {
            throw new ValidationException(
                    String.format("%s is required field", fieldName));
        }
    }
}
