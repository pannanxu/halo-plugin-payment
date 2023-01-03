package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.PaymentDispatcher;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
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
                .flatMap(provider::getPayment)
                .flatMap(pay -> {
                    if (!pay.getOperator().status()) {
                        return Mono.error(new RuntimeException("支付未启用"));
                    }
                    return Mono.just(pay);
                });
    }

    @Override
    public Mono<IPayment> dispatch() {
        return Mono.deferContextual(ctx -> dispatch(ctx.get(PaymentDispatcher.payment).toString()));
    }

    @Override
    public Flux<IPayment> payments() {
        return provider.getPayments()
                .filter(e -> e.getOperator().status());
    }
}
