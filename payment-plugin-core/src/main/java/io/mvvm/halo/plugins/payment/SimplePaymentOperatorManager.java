package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.IPaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.PaymentDescriptor;
import io.mvvm.halo.plugins.payment.sdk.PaymentDescriptorGetter;
import io.mvvm.halo.plugins.payment.sdk.exception.PaymentNotFoundException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SimplePaymentProvider.
 *
 * @author: pan
 **/
@Slf4j
public class SimplePaymentOperatorManager implements PaymentOperatorManager {

    private final Map<String, Wrapper> PAYMENT_CONTAINER = new ConcurrentHashMap<>();

    private final PaymentFactory factory;

    public SimplePaymentOperatorManager(PaymentFactory factory) {
        this.factory = factory;
    }

    @Override
    public IPayment register(IPaymentOperator operator) {
        if (PAYMENT_CONTAINER.containsKey(operator.getDescriptor().getName())) {
            unregister(operator);
        }
        IPayment payment = factory.createPayment(operator);
        PAYMENT_CONTAINER.put(payment.getDescriptor().getName(), new Wrapper(operator, payment));
        log.debug("register payment: {}", payment.getDescriptor().getName());
        return payment;
    }

    @Override
    public void unregister(IPaymentOperator operator) {
        PaymentDescriptor descriptor = operator.getDescriptor();
        Wrapper wrapper = PAYMENT_CONTAINER.remove(descriptor.getName());
        log.debug("unregister payment: {}", operator.getDescriptor().getName());
        if (null != wrapper && null != wrapper.operator) {
            wrapper.operator.destroy();
        }
    }

    @Override
    public Mono<IPaymentOperator> getOperator(String name) {
        return Mono.fromSupplier(() -> PAYMENT_CONTAINER.get(name))
                .switchIfEmpty(Mono.defer(() -> Mono.justOrEmpty(PAYMENT_CONTAINER.get(name.split("-")[0]))))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new PaymentNotFoundException("???????????????????????????"))))
                .map(Wrapper::operator);
    }

    @Override
    public Mono<IPayment> getPayment(String name) {
        return Mono.fromSupplier(() -> PAYMENT_CONTAINER.get(name))
                // ????????????????????????????????????????????????
                // ???????????????????????????????????????
                .switchIfEmpty(Mono.defer(() -> Mono.justOrEmpty(PAYMENT_CONTAINER.get(name.split("-")[0]))))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new PaymentNotFoundException("???????????????????????????"))))
                .map(Wrapper::payment)
                .doOnNext(payment -> log.debug("get the payment {} to name {}", payment, name));
    }

    @Override
    public Flux<IPayment> getPayments() {
        return Flux.fromStream(PAYMENT_CONTAINER.values().stream().map(Wrapper::payment));
    }

    @Override
    public void unregister(String pluginId) {
        Iterator<String> iterator = PAYMENT_CONTAINER.keySet().iterator();
        while (iterator.hasNext()) {
            Wrapper wrapper = PAYMENT_CONTAINER.get(iterator.next());
            PaymentDescriptorGetter descriptor = wrapper.payment.getDescriptor();
            if (pluginId.equals(descriptor.getPluginId())) {
                iterator.remove();
                log.debug("unregister payment: {}", descriptor.getName());
            }
        }
    }

    public record Wrapper(IPaymentOperator operator, IPayment payment) {

    }

}
