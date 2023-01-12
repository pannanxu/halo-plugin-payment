package io.mvvm.halo.plugins.payment.sdk.exception;

/**
 * RefundException.
 *
 * @author: pan
 **/
public class CancelException extends BaseException {

    public CancelException(String code, String message, String outTradeNo) {
        super(code, message, outTradeNo);
    }

    public CancelException(String message) {
        super(message);
    }

    public CancelException(String code, String message) {
        super(code, message);
    }

    public CancelException(ExceptionCode code, String message) {
        super(code, message);
    }
}
