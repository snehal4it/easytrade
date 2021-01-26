package com.trade.utils.easytrade.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Common date-time utils
 */
public final class DateTimeUtil {
    public static final DateTimeFormatter TRANSACTION_DATE_TIME_EXCEL_FORMATTER
            = DateTimeFormatter.ofPattern("dd/MM/yyyy h:m:s a");

    public static final DateTimeFormatter TRANSACTION_DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");

    public static final DateTimeFormatter TRANSACTION_DATE_TIME_FORMATTER_V2
            = DateTimeFormatter.ofPattern("M/d/yyyy H:m");

    /** some record doesn't have time associated with it */
    public static final DateTimeFormatter TRANSACTION_DATE_FORMATTER
            = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public static LocalDateTime getTransactionDateTime(String transactionDateTimeStr) {
        try {
            return LocalDateTime.parse(transactionDateTimeStr, TRANSACTION_DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            try {
                // some records are without time
                return LocalDate.parse(transactionDateTimeStr, TRANSACTION_DATE_FORMATTER)
                        .atStartOfDay();
            } catch (DateTimeParseException e2) {
                return LocalDateTime.parse(transactionDateTimeStr, TRANSACTION_DATE_TIME_FORMATTER_V2);
            }
        }
    }

    public static LocalDateTime getTransactionDateTimeWhenAMPMFormat(String transactionDateTimeStr) {
        return LocalDateTime.parse(transactionDateTimeStr, TRANSACTION_DATE_TIME_EXCEL_FORMATTER);
    }

}
