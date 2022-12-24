package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.async.AsyncNotifyHandler;
import io.mvvm.halo.plugins.payment.sdk.async.AsyncNotifyManager;
import run.halo.app.extension.Ref;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SimpleAsyncNotifyManager.
 *
 * @author: pan
 **/
public class SimpleAsyncNotifyManager implements AsyncNotifyManager {

    public static final Map<String, AsyncNotifyHandler> HANDLER_MAP = new ConcurrentHashMap<>();

    @Override
    public void add(AsyncNotifyHandler handler) {
        Ref ref = handler.type();
        HANDLER_MAP.put(ref.getGroup() + ref.getVersion() + ref.getKind(), handler);
    }

    @Override
    public void remove(AsyncNotifyHandler handler) {
        Ref ref = handler.type();
        HANDLER_MAP.remove(ref.getGroup() + ref.getVersion() + ref.getKind());
    }

    @Override
    public AsyncNotifyHandler get(String gvk) {
        return HANDLER_MAP.get(gvk);
    }
}
