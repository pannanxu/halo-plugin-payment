package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.IPaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.PaymentDescriptor;
import io.mvvm.halo.plugins.payment.sdk.PaymentDispatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * PaymentEndpoint.
 *
 * @author: pan
 **/
@Component
public class PaymentEndpoint {

    private final PaymentDispatcher dispatcher;
    private final IAsyncPayment asyncPayment;

    public PaymentEndpoint(PaymentDispatcher dispatcher, IAsyncPayment asyncPayment) {
        this.dispatcher = dispatcher;
        this.asyncPayment = asyncPayment;
    }

    @Bean
    public RouterFunction<ServerResponse> enabledList() {
        return route(GET("/apis/io.mvvm.halo.plugins.payment/list/enabled"),
                request -> {
                    Flux<PaymentDescriptor> descriptorFlux = dispatcher.payments().map(IPayment::getDescriptor);
                    return ServerResponse.ok().body(descriptorFlux, PaymentDescriptor.class);
                });
    }

    @Bean
    public RouterFunction<ServerResponse> initPaymentConfig() {
        return route(GET("/apis/io.mvvm.halo.plugins.payment/init/{name}"),
                request -> {
                    Mono<Boolean> resp = dispatcher.dispatch(request.pathVariable("name"))
                            .map(IPayment::getOperator)
                            .flatMap(IPaymentOperator::initConfig);
                    return ServerResponse.ok().body(resp, Boolean.class);
                });
    }

    @Bean
    public RouterFunction<ServerResponse> paymentNotify() {
        return route(GET("/apis/plugins.payment/notify/{gvk}/{name}/{paymentType}"),
                request -> {
                    String gvk = request.pathVariable("gvk");
                    String paymentType = request.pathVariable("paymentType");
                    Mono<Object> response = asyncPayment.paymentAsyncNotify(request, gvk, paymentType);
                    return ServerResponse.ok().body(response, Object.class);
                });
    }

}
