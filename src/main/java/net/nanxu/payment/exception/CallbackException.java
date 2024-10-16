package net.nanxu.payment.exception;

/**
 * CallbackException.
 *
 * @author: P
 **/
public class CallbackException extends RuntimeException {
    public CallbackException() {
    }

    public CallbackException(String message) {
        super(message);
    }

    public CallbackException(String message, Throwable cause) {
        super(message, cause);
    }

    public CallbackException(Throwable cause) {
        super(cause);
    }

    public CallbackException(String message, Throwable cause, boolean enableSuppression,
                             boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
