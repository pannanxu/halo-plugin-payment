package io.mvvm.halo.plugins.payment.callback;

import io.mvvm.halo.plugins.payment.sdk.NotifyCallback;

/**
 * BizPaymentProvider.
 *
 * @author: pan
 **/
public interface NotifyCallbackManager {

    void register(NotifyCallback point);

    void unregister(NotifyCallback point);

    NotifyCallback getPoint(String gvk);
}
