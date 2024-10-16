package net.nanxu.payment.exception;

/**
 * PaymentException.
 *
 * @author: P
 **/
public class PaymentException extends RuntimeException {
    public PaymentException() {
    }

    public PaymentException(String message) {
        super(message);
    }

    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }

    public PaymentException(Throwable cause) {
        super(cause);
    }

    public PaymentException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
