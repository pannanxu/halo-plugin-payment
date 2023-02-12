package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.callback.SimpleNotifyCallbackManager;
import io.mvvm.halo.plugins.payment.endpoint.PaymentEndpoint;
import io.mvvm.halo.plugins.payment.sdk.PaymentDispatcher;
import io.mvvm.halo.plugins.payment.sdk.PaymentRegister;
import io.mvvm.halo.plugins.payment.sdk.SdkContextHolder;
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.List;

/**
 * PaymentConfig.
 *
 * @author: pan
 **/
@Configuration
public class PaymentConfiguration {

    private final PaymentOperatorManager provider;
    private final SimpleNotifyCallbackManager notifyProvider;

    public PaymentConfiguration(PaymentFactory factory) {
        this.provider = new SimplePaymentOperatorManager(factory);
        this.notifyProvider = new SimpleNotifyCallbackManager();
    }

    @Bean
    public PaymentRegister paymentRegister() {
        return new PaymentOperatorRegister(provider, notifyProvider);
    }

    @Bean
    public PaymentDispatcher paymentDispatcher() {
        return new SimplePaymentDispatcher(provider);
    }

    @Bean
    public IAsyncPayment asyncPayment(PaymentDispatcher dispatcher) {
        return new AsyncPayment(dispatcher, notifyProvider);
    }

    @Bean
    public SdkContextHolder sdkContext() {
        return new SdkContextHolder();
    }

    @Bean
    public RouterFunction<ServerResponse> paymentEndpoints(PaymentDispatcher dispatcher,
                                                           IAsyncPayment asyncPayment,
                                                           List<PaymentEndpoint> list) {
        SpringdocRouteBuilder routeBuilder = SpringdocRouteBuilder.route();
        list.stream()
                .peek(e -> {
                    e.setAsyncPayment(asyncPayment);
                    e.setProvider(provider);
                    e.setDispatcher(dispatcher);
                })
                .forEach(e -> routeBuilder.nest(RequestPredicates.path(e.groupVersion().toString()),
                        e::endpoint,
                        builder -> builder.operationId("PaymentCustomEndpoints")
                                .description("Payment Custom Endpoint")
                                .tag("/PaymentCustomEndpoint")));
        return routeBuilder.build();
    }

}
