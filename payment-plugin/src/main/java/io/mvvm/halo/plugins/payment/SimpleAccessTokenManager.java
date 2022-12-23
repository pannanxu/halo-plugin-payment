package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.AccessToken;
import io.mvvm.halo.plugins.payment.sdk.AccessTokenManager;
import io.mvvm.halo.plugins.payment.sdk.AccessTokenRefresher;
import io.mvvm.halo.plugins.payment.sdk.AccessTokenWrapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AccessTokenCache.
 *
 * @author: pan
 **/
public class SimpleAccessTokenManager implements AccessTokenManager {

    private final Map<String, AccessTokenWrapper> map = new ConcurrentHashMap<>();

    @Override
    public void addRefresher(AccessTokenRefresher refresher) {
        map.put(refresher.type().getName(), new AccessTokenWrapper(refresher));
    }

    @Override
    public void removeRefresher(String name) {
        map.remove(name);
    }

    @Override
    public AccessToken getAccessToken(String name) {
        AccessTokenWrapper wrapper = map.get(name);
        if (null == wrapper) {
            return null;
        }
        if (wrapper.isExpired()) {
            wrapper.refresh();
        }
        return wrapper.getAccessToken();
    }

}
