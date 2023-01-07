package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.PayEnvironmentFetcher;
import io.mvvm.halo.plugins.payment.sdk.PaymentDispatcher;
import io.mvvm.halo.plugins.payment.sdk.PaymentRegister;
import io.mvvm.halo.plugins.payment.sdk.SdkContext;
import io.mvvm.halo.plugins.payment.sdk.accesstoken.AccessTokenManager;
import io.mvvm.halo.plugins.payment.sdk.async.AsyncNotifyManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.infra.ExternalUrlSupplier;

/**
 * PaymentConfig.
 *
 * @author: pan
 **/
@Configuration
public class PaymentConfiguration {

    private final PaymentProvider provider;

    public PaymentConfiguration(ExternalUrlSupplier externalUrlSupplier,
                                ApplicationContext applicationContext) {
        this.provider = new SimplePaymentProvider(externalUrlSupplier);
        SdkContext.paymentCtx = applicationContext;
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

}
