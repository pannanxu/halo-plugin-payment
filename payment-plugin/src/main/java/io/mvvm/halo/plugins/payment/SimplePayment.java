package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.IPaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.PaymentResponseWrapper;
import io.mvvm.halo.plugins.payment.sdk.simple.CreatePaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.simple.CreatePaymentResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import run.halo.app.extension.Ref;
import run.halo.app.infra.ExternalUrlSupplier;

/**
 * SimplePayment.
 *
 * @author: pan
 **/
@Slf4j
public class SimplePayment implements IPayment {

    private final Ref type;
    private final ExternalUrlSupplier externalUrlSupplier;
    @Getter
    private IPaymentOperator operator;

    public SimplePayment(IPaymentOperator operator, Ref type, ExternalUrlSupplier externalUrlSupplier) {
        this.operator = operator;
        this.type = type;
        this.externalUrlSupplier = externalUrlSupplier;
    }

    @Override
    public Ref type() {
        return type;
    }

    @Override
    public Mono<PaymentResponseWrapper<CreatePaymentResponse>> create(CreatePaymentRequest request) {
        request.setNotifyDomain(externalUrlSupplier.get().toString());
        return getOperator().create(request)
                .map(response -> new PaymentResponseWrapper<>(response, type()));
    }

    @Override
    public String toString() {
        return """
                SimplePayment: %s, name: %s
                """.formatted(operator, operator.type().getName());
    }
}
