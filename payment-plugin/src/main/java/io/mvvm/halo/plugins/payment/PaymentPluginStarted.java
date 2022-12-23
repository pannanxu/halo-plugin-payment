package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.code.CodePaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.AccessTokenManager;
import io.mvvm.halo.plugins.payment.sdk.PaymentRegister;

/**
 * PaymentPluginStarted.
 *
 * @author: pan
 **/
public class PaymentPluginStarted {

    private final PaymentRegister register;
    private final AccessTokenManager accessTokenManager;
    private final CodePaymentOperator codePaymentOperator;

    public PaymentPluginStarted(PaymentRegister register,
                                AccessTokenManager accessTokenManager, 
                                CodePaymentOperator codePaymentOperator) {
        this.register = register;
        this.accessTokenManager = accessTokenManager;
        this.codePaymentOperator = codePaymentOperator;
    }

    public void start() {
        register.register(codePaymentOperator);
    }

    public void stop() {
        register.unregister(codePaymentOperator);
    }

}
