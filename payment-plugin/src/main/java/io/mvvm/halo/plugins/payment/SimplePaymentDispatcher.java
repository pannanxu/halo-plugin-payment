package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.PaymentDispatcher;
import io.mvvm.halo.plugins.payment.sdk.PaymentProvider;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * PaymentWrapper. 支付调度
 *
 * @author: pan
 **/
@Slf4j
public class SimplePaymentDispatcher implements PaymentDispatcher {

    private final PaymentProvider provider;

    public SimplePaymentDispatcher(PaymentProvider provider) {
        this.provider = provider;
    }

    @Override
    public Mono<IPayment> dispatch(String payment) {
        return Mono.just(payment)
                .flatMap(provider::getPayment);
    }

    @Override
    public Mono<IPayment> dispatch() {
        return Mono.deferContextual(ctx -> dispatch(ctx.get(PaymentDispatcher.payment).toString()));
    }
}
