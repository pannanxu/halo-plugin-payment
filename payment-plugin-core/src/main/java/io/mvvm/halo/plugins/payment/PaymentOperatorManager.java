package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.IPaymentOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * PaymentProvider.
 *
 * @author: pan
 **/
public interface PaymentOperatorManager {

    IPayment register(IPaymentOperator operator);

    void unregister(IPaymentOperator operator);

    Mono<IPaymentOperator> getOperator(String name);

    Mono<IPayment> getPayment(String name);

    Flux<IPayment> getPayments();

    void unregister(String pluginId);
}
