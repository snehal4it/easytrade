package com.trade.utils.easytrade.exception;

/**
 * Validation exception
 */
public class ValidationException extends RuntimeException {

    /**
     * create new validation exception
     * @param message error message
     */
    public ValidationException(String message) {
        super(message);
    }
}
