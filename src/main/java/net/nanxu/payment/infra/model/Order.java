package net.nanxu.payment.infra.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;
import run.halo.app.extension.Ref;

/**
 * Order.
 *
 * @author: P
 **/
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@GVK(kind = "Order", group = "order.payment.plugin.nanxu.net", version = "v1alpha1", singular = 
        "order", plural = "orders")
public class Order extends AbstractExtension {
    /**
     * 订单单号
     */
    private String orderNo;
    /**
     * 订单标题
     */
    private String subject;
    /**
     * 订单说明
     */
    private String description;
    /**
     * 订单产品
     */
    private List<Product> products;
    /**
     * 订单总金额
     */
    private Money money;
    /**
     * 业务插件
     */
    private Ref business;
    /**
     * 支付插件
     */
    private Ref payment;
    /**
     * 支付用户
     */
    private Ref user;
    /**
     * 支付状态
     */
    private PayStatus payStatus;
    /**
     * 退款状态
     */
    private RefundStatus refundStatus;

    private Map<String, Object> extra;

    public Order addExtra(String key, Object value) {
        if (extra == null) {
            extra = new HashMap<>();
        } else {
            extra.put(key, value);
        }
        return this;
    }

    @Data
    public static class Product {
        /**
         * 商品编号
         */
        private String productNo;
        /**
         * 商品标题
         */
        private String title;
        /**
         * 商品描述
         */
        private String description;
        /**
         * 商品价格
         */
        private Money money;
        /**
         * 商品数量
         */
        private Long quantity;
        /**
         * 商品类型
         */
        private String itemType;
        /**
         * 商品链接
         */
        private String productUrl;
        /**
         * 商品图片链接
         */
        private String imageUrl;
    }

    public enum PayStatus {
        /**
         * 未支付
         */
        UNPAID,
        /**
         * 支付中
         */
        PAYING,
        /**
         * 支付成功
         */
        PAID,
        /**
         * 支付失败
         */
        FAILED
    }

    public enum RefundStatus {
        /**
         * 未退款
         */
        UNREFUNDED,
        /**
         * 退款申请中
         */
        REFUNDING,
        /**
         * 退款成功
         */
        REFUNDED,
        /**
         * 退款失败
         */
        REFUND_FAILED
    }

    public static class PluginRef {

        private Ref plugin;

        private String notifyUrl;

    }

    public static Order createOrder() {
        return new Order()
            .setPayStatus(Order.PayStatus.UNPAID)
            .setRefundStatus(Order.RefundStatus.UNREFUNDED);
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
