package io.mvvm.halo.plugins.payment.sdk;

/**
 * 支付模式.
 * <p>
 * 当创建一个订单后，第三方支付可能会返回二维码，也可能会返回跳转地址
 *
 * @author: pan
 **/
public enum PaymentMode {
    /**
     * 无需处理
     */
    none,
    /**
     * 二维码图片, base64 格式
     */
    qr_img,
    /**
     * 二维码内容, 前端需自行生成二维码
     */
    qr_code,
    /**
     * h5 页面跳转支付
     */
    h5_url
}
