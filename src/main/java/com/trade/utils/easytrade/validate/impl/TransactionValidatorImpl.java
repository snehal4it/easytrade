package com.trade.utils.easytrade.validate.impl;

import com.trade.utils.easytrade.exception.ValidationException;
import com.trade.utils.easytrade.model.Transaction;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Named;
import java.util.List;

import static com.trade.utils.easytrade.util.Constant.TRANSACTION_TYPES;

@Named
public class TransactionValidatorImpl {

    public void validateTransactions(List<Transaction> transactions) {
        transactions.stream().forEach(this::validateTransaction);
    }

    public void validateTransaction(Transaction transaction) {
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
