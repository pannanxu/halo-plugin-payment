package io.mvvm.halo.plugins.payment.endpoint;

import io.mvvm.halo.plugins.payment.IAsyncPayment;
import io.mvvm.halo.plugins.payment.PaymentProvider;
import io.mvvm.halo.plugins.payment.sdk.PaymentDispatcher;
import run.halo.app.core.extension.endpoint.CustomEndpoint;

/**
 * PaymentEndpoint.
 *
 * @author: pan
 **/
public interface PaymentEndpoint extends CustomEndpoint {

    default void setDispatcher(PaymentDispatcher dispatcher) {
        
    }

    default void setAsyncPayment(IAsyncPayment asyncPayment) {
        
    }

    default void setProvider(PaymentProvider provider) {
        
    }
    
}
