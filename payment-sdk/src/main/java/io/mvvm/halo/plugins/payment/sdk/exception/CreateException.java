package io.mvvm.halo.plugins.payment.sdk.exception;

/**
 * CreateException.
 *
 * @author: pan
 **/
public class CreateException extends BaseException {

    public CreateException(String code, String message, String outTradeNo) {
        super(code, message, outTradeNo);
    }

    public CreateException(String message) {
        super(message);
    }

    public CreateException(String code, String message) {
        super(code, message);
    }

    public CreateException(ExceptionCode code, String message) {
        super(code, message);
    }
}
