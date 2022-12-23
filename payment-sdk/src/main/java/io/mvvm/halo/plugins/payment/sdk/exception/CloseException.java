package io.mvvm.halo.plugins.payment.sdk.exception;

import io.mvvm.halo.plugins.payment.sdk.PaymentRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * CloseException.
 *
 * @author: pan
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class CloseException extends RuntimeException {

    private PaymentRequest request;

    public CloseException(PaymentRequest request) {
        this.request = request;
    }

    public CloseException(String message, PaymentRequest request) {
        super(message);
        this.request = request;
    }

    public CloseException(String message, Throwable cause, PaymentRequest request) {
        super(message, cause);
        this.request = request;
    }

    public CloseException(Throwable cause, PaymentRequest request) {
        super(cause);
        this.request = request;
    }

    public CloseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, PaymentRequest request) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.request = request;
    }
}
