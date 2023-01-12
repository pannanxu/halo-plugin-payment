package io.mvvm.halo.plugins.payment.sdk.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * RefundException.
 *
 * @author: pan
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException {

    private String code = ExceptionCode.error.name();
    private String outTradeNo;

    public BaseException(String code, String message, String outTradeNo) {
        super(message);
        this.code = code;
        this.outTradeNo = outTradeNo;
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BaseException(ExceptionCode code, String message) {
        super(message);
        this.code = code.name();
    }
}
