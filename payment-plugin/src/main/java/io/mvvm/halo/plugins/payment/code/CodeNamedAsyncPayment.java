package io.mvvm.halo.plugins.payment.code;

import io.mvvm.halo.plugins.payment.sdk.PaymentDispatcher;
import io.mvvm.halo.plugins.payment.sdk.async.AsyncNotifyManager;
import io.mvvm.halo.plugins.payment.sdk.async.NamedAsyncPayment;
import org.springframework.stereotype.Component;

/**
 * CodeNamedAsyncPayment.
 *
 * @author: pan
 **/
@Component
public class CodeNamedAsyncPayment extends NamedAsyncPayment {
    
    public CodeNamedAsyncPayment(PaymentDispatcher dispatcher, AsyncNotifyManager asyncNotifyManager) {
        super(dispatcher, asyncNotifyManager);
    }

    @Override
    protected String named() {
        return CodeExtension.group;
    }
}
