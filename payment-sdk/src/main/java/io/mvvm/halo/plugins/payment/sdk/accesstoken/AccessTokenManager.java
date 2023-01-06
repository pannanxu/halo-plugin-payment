package io.mvvm.halo.plugins.payment.sdk.accesstoken;

/**
 * AccessTokenManager.
 *
 * @author: pan
 **/
public interface AccessTokenManager {

    void addRefresher(AccessTokenRefresher refresher);
    void removeRefresher(String name);

    AccessToken getAccessToken(String name);
}
