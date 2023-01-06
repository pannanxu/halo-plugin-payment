package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.IPaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.PaymentDescriptor;
import io.mvvm.halo.plugins.payment.sdk.exception.PaymentNotFoundException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.infra.ExternalUrlSupplier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SimplePaymentProvider.
 *
 * @author: pan
 **/
@Slf4j
public class SimplePaymentProvider implements PaymentProvider {

    private final Map<String, IPayment> PAYMENT_CONTAINER = new ConcurrentHashMap<>();

    private final ExternalUrlSupplier externalUrlSupplier;

    public SimplePaymentProvider(ExternalUrlSupplier externalUrlSupplier) {
        this.externalUrlSupplier = externalUrlSupplier;
    }

    @Override
    public IPayment register(IPaymentOperator operator) {
        IPayment payment = createPayment(operator);
        PAYMENT_CONTAINER.put(payment.getDescriptor().getName(), payment);
        log.debug("register payment: {}", payment.getDescriptor().getName());
        return payment;
    }

    private IPayment createPayment(IPaymentOperator operator) {
        PaymentDescriptor type = operator.getDescriptor();
        if (PAYMENT_CONTAINER.containsKey(type.getName())) {
            unregister(operator);
        }
        return new SimplePayment(operator, type, externalUrlSupplier);
    }

    @Override
    public void unregister(IPaymentOperator operator) {
        PaymentDescriptor descriptor = operator.getDescriptor();
        IPayment payment = PAYMENT_CONTAINER.remove(descriptor.getName());
        log.debug("unregister payment: {}", descriptor.getName());
        if (null != payment && null != payment.getOperator()) {
            payment.getOperator().destroy();
        }
    }

    @Override
    public Mono<IPayment> getPayment(String name) {
        return Mono.fromSupplier(() -> PAYMENT_CONTAINER.get(name))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new PaymentNotFoundException())))
                .doOnNext(payment -> log.debug("get the payment {} to name {}", payment, name));
    }

    @Override
    public Flux<IPaymentOperator> getPaymentOperators() {
        return Flux.fromStream(PAYMENT_CONTAINER.values()
                .stream()
                .map(IPayment::getOperator));
    }

    @Override
    public Flux<IPayment> getPayments() {
        return Flux.fromStream(PAYMENT_CONTAINER.values().stream());
    }

}
