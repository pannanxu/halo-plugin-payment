package net.nanxu.order;

import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

/**
 * Order.
 *
 * @author: P
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@GVK(kind = "Order", group = "order.payment.plugin.nanxu.net", version = "v1alpha1", singular = "order", plural = "orders")
public class Order extends AbstractExtension {

    private String orderId;

    private Profile profile;

    private Money money;

    private String payType;

    @Data
    public static class Profile {
        private String userId;
        private String subject;
        private String description;
        private String body;
    }

    @Data
    public static class Money {
        private BigDecimal amount;
        private String currency = "CNY";
    }

    /**
     * 创建订单.
     */
    public void create() {

    }

    /**
     * 取消订单.
     */
    public void cancel() {

    }

    /**
     * 创建退款申请单
     */
    public void applyRefund() {

    }

    /**
     * 发起支付
     */
    public void payment() {
        
    }

}
