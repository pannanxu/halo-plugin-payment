package io.mvvm.halo.plugins.payment.sdk.exception;

/**
 * FetchException.
 *
 * @author: pan
 **/
public class FetchException extends BaseException {

    public FetchException(String code, String message, String outTradeNo) {
        super(code, message, outTradeNo);
    }

    public FetchException(String message) {
        super(message);
    }

    public FetchException(String code, String message) {
        super(code, message);
    }
}
