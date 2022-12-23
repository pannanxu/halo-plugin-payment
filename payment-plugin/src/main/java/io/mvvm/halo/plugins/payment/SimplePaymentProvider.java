package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.IPaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.PaymentExtension;
import io.mvvm.halo.plugins.payment.sdk.PaymentProvider;
import io.mvvm.halo.plugins.payment.sdk.exception.PaymentNotFoundException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.GVK;
import run.halo.app.extension.Ref;

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

    @Override
    public IPayment register(IPaymentOperator operator) {
        Ref type = operator.type();
        if (PAYMENT_CONTAINER.containsKey(type.getName())) {
            unregister(operator);
        }
        GVK gvk = PaymentExtension.class.getAnnotation(GVK.class);
        type.setGroup(gvk.group());
        type.setKind(gvk.kind());
        type.setVersion(gvk.version());
        IPayment payment = new SimplePayment(operator, type);
        PAYMENT_CONTAINER.put(payment.type().getName(), payment);
        log.debug("register payment: {}", type.getName());
        return payment;
    }

    @Override
    public void unregister(IPaymentOperator operator) {
        IPayment payment = PAYMENT_CONTAINER.remove(operator.type().getName());
        log.debug("unregister payment: {}", operator.type().getName());
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
    public Flux<IPaymentOperator> getPayments() {
        return Flux.concat(PAYMENT_CONTAINER.values()
                .stream()
                .map(IPayment::getOperatorReactive)
                .toList());
    }
}
