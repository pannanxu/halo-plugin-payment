package io.mvvm.halo.plugins.payment.alipay;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * AliPaymentSetting.
 *
 * @author: pan
 **/
@Data
public class AliPaymentSetting {
    
    public static final String PUB_MODE = "pub";
    public static final String CERT_MODE = "cert";

    public static final String NAME = "alipay-payment-settings";
    public static final String GROUP = "alipay";
    @Schema(title = "网关地址")
    private String serverUrl;
    @Schema(title = "应用ID")
    private String appId;
    @Schema(title = "应用私钥")
    private String privateKey;

    @Schema(title = "模式", description = "pub：公钥模式；cert：证书模式")
    private String mode;

    @Schema(title = "支付宝公钥", description = "公钥模式")
    private String alipayPublicKey;

    @Schema(title = "支付宝公钥证书", description = "证书模式。file:// 开头则寻找本地路径")
    private String alipayPublicCert;
    @Schema(title = "应用公钥证书SN", description = "证书模式。file:// 开头则寻找本地路径")
    private String appCert;
    @Schema(title = "支付宝根证书SN", description = "证书模式。file:// 开头则寻找本地路径")
    private String alipayRootCert;

    @Schema(title = "敏感信息对称加密算法密钥")
    private String encryptKey;

    public boolean isCertMode() {
        return CERT_MODE.equals(mode);
    }
}
