package io.mvvm.halo.plugins.payment.sdk.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * PaymentNotFoundException.
 *
 * @author: pan
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentNotFoundException extends RuntimeException {
    
    public PaymentNotFoundException() {
        super("暂无处理器");
    }

    public PaymentNotFoundException(String message) {
        super(message);
    }

    public PaymentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PaymentNotFoundException(Throwable cause) {
        super(cause);
    }

    public PaymentNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
