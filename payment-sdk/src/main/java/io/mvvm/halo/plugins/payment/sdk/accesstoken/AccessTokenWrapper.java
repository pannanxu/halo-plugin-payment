package io.mvvm.halo.plugins.payment.sdk.accesstoken;

import lombok.Data;

/**
 * AccessTokenWrapper.
 *
 * @author: pan
 **/
@Data
public class AccessTokenWrapper {
    /**
     * accessToken 刷新处理器
     */
    private AccessTokenRefresher refresher;
    /**
     * accessToken
     */
    private AccessToken accessToken;
    /**
     * accessToken 过期时间
     */
    private long expireAt;

    public AccessTokenWrapper(AccessTokenRefresher refresher) {
        this(refresher.refresh());
        this.refresher = refresher;
    }

    private AccessTokenWrapper(AccessToken accessToken) {
        this.accessToken = accessToken;
        this.expireAt = accessToken.getExpire() + (System.currentTimeMillis() / 1000);
    }

    /**
     * @return accessToken 是否过期
     */
    public boolean isExpired() {
        return (System.currentTimeMillis() / 1000) - expireAt <= 0;
    }

    /**
     * 执行刷新AccessToken
     */
    public void refresh() {
        AccessToken refresh = refresher.refresh();
        this.accessToken.refresh(refresh);
        this.expireAt = accessToken.getExpire() + (System.currentTimeMillis() / 1000);
    }
}
