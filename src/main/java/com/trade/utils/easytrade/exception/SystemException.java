package com.trade.utils.easytrade.exception;

/**
 * Wrapper to convert checked exception to runtime exception
 * situation when can't continue processing
 */
public class SystemException extends RuntimeException {
    public SystemException(String message, Exception e) {
        super(message, e);
    }
}
