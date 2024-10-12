package net.nanxu.payment.core.model;

import lombok.Data;
import net.nanxu.order.Order;

/**
 * PaymentResult.
 *
 * @author: P
 **/
@Data
public class PaymentResult {

    private String status;

    private String type;

    private String content;

    private Order order;

    public enum Status {
        /**
         * 发起支付成功
         */
        SUCCESS,
        /**
         * 发起支付失败
         */
        FAILURE
    }

    public enum Type {
        /**
         * 扫码支付（base64二维码图片）
         */
        QRCode,
        /**
         * 跳转支付
         */
        Redirect,
        /**
         * 透传给客户端SDK
         */
        Other
    }

}
