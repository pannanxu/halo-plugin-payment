package io.mvvm.halo.plugins.payment.wechat;

import com.wechat.pay.java.core.RSAAutoCertificateConfig;
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

    /**
     * 根据 商户API私钥 前缀去决定是构建私钥内容还是私钥路径
     *
     * @param builder WeChat sdk config
     */
    public void privateKey(RSAAutoCertificateConfig.Builder builder) {
        if (null != this.getPrivateKey()
            && this.getPrivateKey().startsWith(PaymentSetting.LOCAL_FILE_PREFIX)) {
            builder.privateKeyFromPath(this.getPrivateKey().replace(PaymentSetting.LOCAL_FILE_PREFIX, ""));
        } else {
            builder.privateKey(this.getPrivateKey());
        }
    }
}
