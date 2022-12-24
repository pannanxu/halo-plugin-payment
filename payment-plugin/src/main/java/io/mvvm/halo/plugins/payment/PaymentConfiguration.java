package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.code.CodePaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.AccessTokenManager;
import io.mvvm.halo.plugins.payment.sdk.async.AsyncNotifyManager;
import io.mvvm.halo.plugins.payment.sdk.PaymentDispatcher;
import io.mvvm.halo.plugins.payment.sdk.PaymentProvider;
import io.mvvm.halo.plugins.payment.sdk.PaymentRegister;
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
    public PaymentPluginStarted paymentPluginStarted(PaymentRegister register,
                                                     AccessTokenManager accessTokenManager,
                                                     CodePaymentOperator codePaymentOperator) {
        return new PaymentPluginStarted(register, accessTokenManager, codePaymentOperator);
    }

    @Bean
    public AsyncNotifyManager asyncNotifyManager() {
        return new SimpleAsyncNotifyManager();
    }
}
