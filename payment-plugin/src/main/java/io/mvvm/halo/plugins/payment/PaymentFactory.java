package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.rule.LimitRuleContext;
import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.IPaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.PayEnvironmentFetcher;
import io.mvvm.halo.plugins.payment.sdk.PaymentDescriptor;
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
    private final LimitRuleContext limitRuleContext;
    private final CacheManager cacheManager;

    public PaymentFactory(ExternalUrlSupplier externalUrlSupplier,
                          ReactiveExtensionClient client,
                          PayEnvironmentFetcher fetcher,
                          LimitRuleContext limitRuleContext,
                          CacheManager cacheManager) {
        this.externalUrlSupplier = externalUrlSupplier;
        this.client = client;
        this.fetcher = fetcher;
        this.limitRuleContext = limitRuleContext;
        this.cacheManager = cacheManager;
    }

    public IPayment createPayment(IPaymentOperator operator) {
        PaymentDescriptor type = operator.getDescriptor();
        IPayment payment = new SimplePayment(operator, type, externalUrlSupplier, fetcher);
        return new PaymentRuleDecorator(payment, client, fetcher, limitRuleContext, cacheManager);
    }

}
