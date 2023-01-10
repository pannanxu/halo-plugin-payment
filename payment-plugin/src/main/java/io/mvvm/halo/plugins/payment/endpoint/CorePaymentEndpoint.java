package io.mvvm.halo.plugins.payment.endpoint;

import io.mvvm.halo.plugins.payment.PaymentProvider;
import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.IPaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.PaymentDescriptor;
import io.mvvm.halo.plugins.payment.sdk.PaymentDispatcher;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.GroupVersion;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * InitPaymentEndpoint.
 *
 * @author: pan
 **/
@Setter
@Component
public class CorePaymentEndpoint implements PaymentEndpoint {

    private PaymentDispatcher dispatcher;
    private PaymentProvider provider;

    @Override
    public RouterFunction<ServerResponse> endpoint() {
        return route(GET("/init/{name}"), this::init)
                .and(route(GET("/list/enabled"), this::list))
                .and(route(GET("/list/all"), this::listAll));
    }

    @Override
    public GroupVersion groupVersion() {
        return new GroupVersion("io.mvvm.halo.plugins.payment", "v1");
    }

    Mono<ServerResponse> init(ServerRequest request) {
        Mono<Boolean> resp = provider.getOperator(request.pathVariable("name"))
                .flatMap(IPaymentOperator::initConfig);
        return ServerResponse.ok().body(resp, Boolean.class);
    }

    Mono<ServerResponse> list(ServerRequest request) {
        String device = request.queryParam("device").orElse(null);
        Flux<PaymentDescriptor> descriptorFlux = dispatcher.payments(device).map(IPayment::getDescriptor);
        return ServerResponse.ok().body(descriptorFlux, PaymentDescriptor.class);
    }

    Mono<ServerResponse> listAll(ServerRequest request) {
        Flux<PaymentDescriptor> descriptorFlux = provider.getPayments().map(IPayment::getDescriptor);
        return ServerResponse.ok().body(descriptorFlux, PaymentDescriptor.class);
    }

}
