package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.IPaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.PayEnvironmentFetcher;
import io.mvvm.halo.plugins.payment.sdk.cache.CacheManager;
import org.springframework.stereotype.Component;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.infra.ExternalUrlSupplier;

/**
 * PaymentFactory.
 *
 * @author: pan
 **/
@Component
public class PaymentFactory {

    private final ExternalUrlSupplier externalUrlSupplier;
    private final ReactiveExtensionClient client;
    private final PayEnvironmentFetcher fetcher;
    private final CacheManager cacheManager;

    public PaymentFactory(ExternalUrlSupplier externalUrlSupplier,
                          ReactiveExtensionClient client,
                          PayEnvironmentFetcher fetcher,
                          CacheManager cacheManager) {
        this.externalUrlSupplier = externalUrlSupplier;
        this.client = client;
        this.fetcher = fetcher;
        this.cacheManager = cacheManager;
    }

    public IPayment createPayment(IPaymentOperator operator) {
        return new SimplePayment(operator, externalUrlSupplier, fetcher, cacheManager, client);
    }

}
