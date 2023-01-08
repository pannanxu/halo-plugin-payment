package io.mvvm.halo.plugins.payment.alipay;

import com.alipay.api.AlipayConfig;
import io.mvvm.halo.plugins.payment.sdk.PaymentSetting;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * AliPaymentSetting.
 *
 * @author: pan
 **/
@Data
public class AliPaymentSetting {

    public static final String NAME = "ali-payment-settings";
    public static final String GROUP = "alipay";

    @Schema(title = "模式",
            description = "pub：公钥模式；cert：证书模式")
    private String mode;

    @Schema(title = "支付宝公钥",
            description = "（公钥模式下设置，证书模式下无需设置）")
    private String alipayPublicKey;

    @Schema(title = "支付宝公钥证书",
            description = "（证书模式下设置，公钥模式下无需设置）。file:// 开头则寻找本地路径")
    private String alipayPublicCert;

    @Schema(title = "商户应用公钥证书路径", description = "（证书模式下设置，公钥模式下无需设置）,file:// 开头则寻找本地路径")
    private String appCert;

    @Schema(title = "支付宝根证书路径",
            description = "（证书模式下设置，公钥模式下无需设置）,file:// 开头则寻找本地路径")
    private String rootCert;

    @Schema(title = "支付宝具体sdk配置")
    private AlipayConfig config;

    public void setAlipayPublicCert(String alipayPublicCert) {
        this.alipayPublicCert = alipayPublicCert;
        if (alipayPublicCert.startsWith(PaymentSetting.LOCAL_FILE_PREFIX)) {
            config.setAlipayPublicCertPath(alipayPublicCert.replace(PaymentSetting.LOCAL_FILE_PREFIX, ""));
        } else {
            config.setAlipayPublicCertContent(alipayPublicCert);
        }
    }

    public void setAppCert(String appCert) {
        this.appCert = appCert;
        if (appCert.startsWith(PaymentSetting.LOCAL_FILE_PREFIX)) {
            config.setAppCertPath(appCert.replace(PaymentSetting.LOCAL_FILE_PREFIX, ""));
        } else {
            config.setAppCertContent(appCert);
        }
    }

    public void setRootCert(String rootCert) {
        this.rootCert = rootCert;
        if (rootCert.startsWith(PaymentSetting.LOCAL_FILE_PREFIX)) {
            config.setRootCertPath(rootCert.replace(PaymentSetting.LOCAL_FILE_PREFIX, ""));
        } else {
            config.setRootCertContent(rootCert);
        }
    }
}
