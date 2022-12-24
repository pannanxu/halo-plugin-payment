package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.PaymentDispatcher;
import org.springframework.stereotype.Component;

/**
 * PaymentEndpoint.
 *
 * @author: pan
 **/
@Component
public class PaymentEndpoint {

    public static final String NOTIFY_API = "/apis/io.mvvm.halo.plugins.payment/notify/{name}";

    private final PaymentDispatcher dispatcher;

    public PaymentEndpoint(PaymentDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }


}
