package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.NotifyCallback;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SimpleNotifyCallbackProvider.
 *
 * @author: pan
 **/
@Slf4j
public class SimpleNotifyCallbackProvider implements NotifyCallbackProvider {

    private final Map<String, NotifyCallback> CALLBACK_MAP = new ConcurrentHashMap<>();

    @Override
    public void register(NotifyCallback point) {
        if (CALLBACK_MAP.containsKey(point.getGvk())) {
            unregister(point);
        }
        CALLBACK_MAP.put(point.getGvk(), point);
        log.debug("register notify callback: {}", point.getGvk());
    }

    @Override
    public void unregister(NotifyCallback point) {
        CALLBACK_MAP.remove(point.getGvk());
        log.debug("unregister notify callback: {}", point.getGvk());
    }

    @Override
    public NotifyCallback getPoint(String gvk) {
        return CALLBACK_MAP.get(gvk);
    }
}
