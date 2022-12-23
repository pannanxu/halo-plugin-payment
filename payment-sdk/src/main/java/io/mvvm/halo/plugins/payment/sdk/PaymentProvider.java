package io.mvvm.halo.plugins.payment.sdk;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * PaymentProvider.
 *
 * @author: pan
 **/
public interface PaymentProvider {

    IPayment register(IPaymentOperator operator);

    void unregister(IPaymentOperator operator);

    Mono<IPayment> getPayment(String name);

    Flux<IPaymentOperator> getPayments();
}
