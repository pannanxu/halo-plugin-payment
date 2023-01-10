package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.endpoint.PaymentEndpoint;
import io.mvvm.halo.plugins.payment.sdk.PayEnvironmentFetcher;
import io.mvvm.halo.plugins.payment.sdk.PaymentDispatcher;
import io.mvvm.halo.plugins.payment.sdk.PaymentRegister;
import io.mvvm.halo.plugins.payment.sdk.SdkContextHolder;
import io.mvvm.halo.plugins.payment.sdk.accesstoken.AccessTokenManager;
import io.mvvm.halo.plugins.payment.sdk.async.AsyncNotifyManager;
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.infra.ExternalUrlSupplier;

import java.util.List;

/**
 * PaymentConfig.
 *
 * @author: pan
 **/
@Configuration
public class PaymentConfiguration {

    private final PaymentProvider provider;

    public PaymentConfiguration(ExternalUrlSupplier externalUrlSupplier) {
        this.provider = new SimplePaymentProvider(externalUrlSupplier);
    }

    @Bean
    public PayEnvironmentFetcher payEnvironmentFetcher(ReactiveExtensionClient extensionClient) {
        return new PayEnvironmentFetcher(extensionClient);
    }

    @Bean
    public AccessTokenManager accessTokenManager() {
        return new SimpleAccessTokenManager();
    }

    @Bean
    public PaymentRegister paymentRegister() {
        return new PaymentOperatorRegister(provider);
    }

    @Bean
    public PaymentDispatcher paymentDispatcher() {
        return new SimplePaymentDispatcher(provider);
    }

    @Bean
    public AsyncNotifyManager asyncNotifyManager() {
        return new SimpleAsyncNotifyManager();
    }

    @Bean
    public IAsyncPayment asyncPayment(PaymentDispatcher dispatcher,
                                      AsyncNotifyManager asyncNotifyManager) {
        return new AsyncPayment(dispatcher, asyncNotifyManager);
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
                .forEach(e -> routeBuilder.nest(RequestPredicates.path("/apis/io.mvvm.halo.plugins.payment"),
                        e::endpoint,
                        builder -> builder.operationId("PaymentCustomEndpoints")
                                .description("Payment Custom Endpoint")
                                .tag("/PaymentCustomEndpoint")));
        return routeBuilder.build();
    }

}
