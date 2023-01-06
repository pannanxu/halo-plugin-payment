package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.PaymentSetting;
import lombok.Data;

/**
 * 微信支付设置.
 *
 * @author: pan
 **/
@Data
public class WechatPaymentSetting {

    public static final String NAME = PaymentSetting.name;
    public static final String GROUP = "wechat";

    /**
     * AppId
     */
    private String appId;
    /**
     * 商户号
     */
    private String merchantId;
    /**
     * 商户API私钥
     * <p>
     * RSA 字符串
     * <p>
     * 如果以 "file://" 开头则匹配路径，如："file:///opt/cert/apiclient_key.pem"
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
