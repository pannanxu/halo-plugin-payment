package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.accesstoken.AccessTokenManager;
import io.mvvm.halo.plugins.payment.sdk.PaymentDispatcher;
import io.mvvm.halo.plugins.payment.sdk.PaymentRegister;
import io.mvvm.halo.plugins.payment.sdk.async.AsyncNotifyManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.halo.app.infra.ExternalUrlSupplier;

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
