package net.nanxu.payment.channel.model;

import java.time.Instant;
import lombok.Builder;
import lombok.Data;
import net.nanxu.payment.order.Order;

/**
 * PaymentResult.
 *
 * @author: P
 **/
@Data
@Builder
public class PaymentResult {
    /**
     * 订单创建状态
     */
    private Status status;
    /**
     * 支付类型
     */
    private Type type;
    /**
     * 根据支付类型决定内容是什么
     */
    private String content;
    /**
     * 过期时间
     */
    private Instant expiresAt;
    /**
     * 订单
     */
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
         * <p>
         * 如果提供的content不是以data:image/开头的则会通过二维码生成工具统一处理
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
