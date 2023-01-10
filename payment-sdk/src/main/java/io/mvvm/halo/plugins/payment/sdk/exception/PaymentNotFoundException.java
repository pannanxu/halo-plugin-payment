package io.mvvm.halo.plugins.payment.sdk.exception;

/**
 * PaymentNotFoundException.
 *
 * @author: pan
 **/
public class PaymentNotFoundException extends CreateException {

    public PaymentNotFoundException(String code, String message, String outTradeNo) {
        super(code, message, outTradeNo);
    }

    public PaymentNotFoundException(String message) {
        super(message);
    }

    public PaymentNotFoundException(String code, String message) {
        super(code, message);
    }
}
