package net.nanxu.payment.exception;

/**
 * AccountException.
 *
 * @author: P
 **/
public class AccountException extends RuntimeException {
    public AccountException() {
    }

    public AccountException(String message) {
        super(message);
    }

    public AccountException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountException(Throwable cause) {
        super(cause);
    }

    public AccountException(String message, Throwable cause, boolean enableSuppression,
                            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
