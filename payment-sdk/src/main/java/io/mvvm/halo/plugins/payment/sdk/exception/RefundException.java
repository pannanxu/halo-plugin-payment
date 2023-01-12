package io.mvvm.halo.plugins.payment.sdk.exception;

/**
 * RefundException.
 *
 * @author: pan
 **/
public class RefundException extends BaseException {

    public RefundException(String code, String message, String outTradeNo) {
        super(code, message, outTradeNo);
    }

    public RefundException(String message) {
        super(message);
    }

    public RefundException(String code, String message) {
        super(code, message);
    }

    public RefundException(ExceptionCode code, String message) {
        super(code, message);
    }
}
