package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.IPaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.PaymentDescriptor;
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

    public PaymentFactory(ExternalUrlSupplier externalUrlSupplier, ReactiveExtensionClient client) {
        this.externalUrlSupplier = externalUrlSupplier;
        this.client = client;
    }

    public IPayment createPayment(IPaymentOperator operator) {
        PaymentDescriptor type = operator.getDescriptor();
        IPayment payment = new SimplePayment(operator, type, externalUrlSupplier);
        return new ExtensionPaymentDecorator(payment, client);
    }

}
