package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.NotifyCallback;

/**
 * BizPaymentProvider.
 *
 * @author: pan
 **/
public interface NotifyCallbackProvider {

    void register(NotifyCallback point);

    void unregister(NotifyCallback point);

    NotifyCallback getPoint(String gvk);
}
