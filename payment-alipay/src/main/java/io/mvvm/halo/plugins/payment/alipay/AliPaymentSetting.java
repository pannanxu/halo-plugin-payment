package io.mvvm.halo.plugins.payment.alipay;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

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


    /**
     * 网关地址
     * 线上：https://openapi.alipay.com/gateway.do
     * 沙箱：https://openapi.alipaydev.com/gateway.do
     */
    private String serverUrl = "https://openapi.alipay.com/gateway.do";

    /**
     * 开放平台上创建的应用的ID
     */
    private String appId;

    /**
     * 报文格式，推荐：json
     */
    private String format = "json";

    /**
     * 字符串编码，推荐：utf-8
     */
    private String charset = "utf-8";

    /**
     * 签名算法类型，推荐：RSA2
     */
    private String signType = "RSA2";

    /**
     * 商户私钥
     */
    private String privateKey;

    /**
     * 敏感信息对称加密算法类型，推荐：AES
     */
    private String encryptType = "AES";

    /**
     * 敏感信息对称加密算法密钥
     */
    private String encryptKey;

    /**
     * HTTP代理服务器主机地址
     */
    private String proxyHost;

    /**
     * HTTP代理服务器端口
     */
    private int proxyPort;

    /**
     * 自定义HTTP Header
     */
    private Map<String, String> customHeaders;

    /**
     * 连接超时，单位：毫秒
     */
    private int connectTimeout = 3000;

    /**
     * 读取超时，单位：毫秒
     */
    private int readTimeout = 15000;

    /**
     * 连接池最大空闲连接数
     */
    private int maxIdleConnections = 0;

    /**
     * 存活时间，单位：毫秒
     */
    private long keepAliveDuration = 10000L;


    public void setAlipayPublicCert(String alipayPublicCert) {
        this.alipayPublicCert = alipayPublicCert;
//        if (alipayPublicCert.startsWith(PaymentSetting.LOCAL_FILE_PREFIX)) {
//            setAlipayPublicCertPath(alipayPublicCert.replace(PaymentSetting.LOCAL_FILE_PREFIX, ""));
//        } else {
//            setAlipayPublicCertContent(alipayPublicCert);
//        }
    }

    public void setAppCert(String appCert) {
        this.appCert = appCert;
//        if (appCert.startsWith(PaymentSetting.LOCAL_FILE_PREFIX)) {
//            setAppCertPath(appCert.replace(PaymentSetting.LOCAL_FILE_PREFIX, ""));
//        } else {
//            setAppCertContent(appCert);
//        }
    }

    public void setRootCert(String rootCert) {
        this.rootCert = rootCert;
//        if (rootCert.startsWith(PaymentSetting.LOCAL_FILE_PREFIX)) {
//            setRootCertPath(rootCert.replace(PaymentSetting.LOCAL_FILE_PREFIX, ""));
//        } else {
//            setRootCertContent(rootCert);
//        }
    }
}
