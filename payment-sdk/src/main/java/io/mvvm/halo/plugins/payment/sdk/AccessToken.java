package io.mvvm.halo.plugins.payment.sdk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AccessToken.
 *
 * @author: pan
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessToken {
    /**
     * 支付sdk唯一ID
     */
    private String name;
    /**
     * access token
     */
    private String token;
    /**
     * token 有效时长(秒)
     */
    private int expire;

    public void refresh(AccessToken refresh) {
        this.token = refresh.getToken();
        this.expire = refresh.getExpire();
    }
}
