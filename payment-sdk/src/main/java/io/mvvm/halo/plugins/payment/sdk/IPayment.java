package io.mvvm.halo.plugins.payment.sdk;

import io.mvvm.halo.plugins.payment.sdk.simple.AsyncNotifyResponse;
import io.mvvm.halo.plugins.payment.sdk.simple.CreatePaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.simple.CreatePaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.simple.PaymentInfo;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import run.halo.app.extension.Ref;

/**
 * IPayment.
 *
 * @author: pan
 **/
public interface IPayment {

    Ref type();

    IPaymentOperator getOperator();

    default Mono<IPaymentOperator> getOperatorReactive() {
        return Mono.just(getOperator());
    }

    default Mono<PaymentResponseWrapper<CreatePaymentResponse>> create(CreatePaymentRequest request) {
        return getOperatorReactive()
                .flatMap(payment -> payment.create(request))
                .map(response -> new PaymentResponseWrapper<>(response, type()));
    }

    default Mono<PaymentResponseWrapper<PaymentInfo>> fetch(PaymentRequest request) {
        return getOperatorReactive()
                .flatMap(payment -> payment.fetch(request))
                .map(response -> new PaymentResponseWrapper<>(response, type()));
    }

    default Mono<PaymentResponseWrapper<PaymentResponse>> cancel(PaymentRequest request) {
        return getOperatorReactive()
                .flatMap(payment -> payment.cancel(request))
                .map(response -> new PaymentResponseWrapper<>(response, type()));
    }

    default Mono<PaymentResponseWrapper<PaymentResponse>> refund(PaymentRequest request) {
        return getOperatorReactive()
                .flatMap(payment -> payment.refund(request))
                .map(response -> new PaymentResponseWrapper<>(response, type()));
    }

    default Mono<AsyncNotifyResponse> asyncNotify(ServerRequest request) {
        return getOperatorReactive().flatMap(payment -> payment.asyncNotify(request));
    }

}
