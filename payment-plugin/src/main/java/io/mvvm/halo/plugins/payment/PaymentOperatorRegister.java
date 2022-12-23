package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.IPaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.PaymentProvider;
import io.mvvm.halo.plugins.payment.sdk.PaymentRegister;

/**
 * PaymentOperatorRegister.
 *
 * @author: pan
 **/
public class PaymentOperatorRegister implements PaymentRegister {
    private final PaymentProvider provider;

    public PaymentOperatorRegister(PaymentProvider provider) {
        this.provider = provider;
    }

    @Override
    public IPayment register(IPaymentOperator operator) {
        return provider.register(operator);
    }

    @Override
    public void unregister(IPaymentOperator operator) {
        provider.unregister(operator);
    }

}
