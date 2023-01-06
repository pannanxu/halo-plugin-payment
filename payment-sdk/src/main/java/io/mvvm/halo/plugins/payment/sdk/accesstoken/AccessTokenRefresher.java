package io.mvvm.halo.plugins.payment.sdk.accesstoken;

import run.halo.app.extension.Ref;

/**
 * AccessTokenRef.
 *
 * @author: pan
 **/
public interface AccessTokenRefresher {

    Ref type();

    AccessToken refresh();

}
