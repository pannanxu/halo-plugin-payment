package io.mvvm.halo.plugins.payment.sdk;

/**
 * PaymentRegister.
 *
 * @author: pan
 **/
public interface PaymentRegister {

    IPayment register(IPaymentOperator operator);

    void unregister(IPaymentOperator operator);
}
