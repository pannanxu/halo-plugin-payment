package io.mvvm.halo.plugins.payment;

import lombok.Data;

/**
 * WechatPaymentSetting.
 *
 * @author: pan
 **/
@Data
public class WechatPaymentSetting {


    public static final String NAME = "wechat-payment-settings";
    public static final String GROUP = "basic";

    /**
     * AppId
     */
    private String appId;
    /**
     * 商户号
     */
    private String merchantId;
    /**
     * 商户API私钥, RSA 字符串
     */
    private String privateKey;
    /**
     * 商户证书序列号
     */
    private String merchantSerialNumber;
    /**
     * 商户APIV3密钥
     */
    private String apiV3key;
}
