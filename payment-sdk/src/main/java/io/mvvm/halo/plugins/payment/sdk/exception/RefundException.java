package io.mvvm.halo.plugins.payment.sdk.exception;

import io.mvvm.halo.plugins.payment.sdk.request.PaymentRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * RefundException.
 *
 * @author: pan
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class RefundException extends RuntimeException {

    private PaymentRequest request;

    public RefundException(PaymentRequest request) {
        this.request = request;
    }

    public RefundException(String message, PaymentRequest request) {
        super(message);
        this.request = request;
    }

    public RefundException(String message, Throwable cause, PaymentRequest request) {
        super(message, cause);
        this.request = request;
    }

    public RefundException(Throwable cause, PaymentRequest request) {
        super(cause);
        this.request = request;
    }

    public RefundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, PaymentRequest request) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.request = request;
    }
}
