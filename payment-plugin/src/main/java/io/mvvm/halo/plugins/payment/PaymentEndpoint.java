package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.PaymentDispatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

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
