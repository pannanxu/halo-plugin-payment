package io.mvvm.halo.plugins.payment.sdk.async;

/**
 * AsyncNotifyManager.
 *
 * @author: pan
 **/
public interface AsyncNotifyManager {
    
    void add(AsyncNotifyHandler handler);
    
    void remove(AsyncNotifyHandler handler);

    AsyncNotifyHandler get(String gvk);
}
