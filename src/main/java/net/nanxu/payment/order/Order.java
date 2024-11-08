package net.nanxu.payment.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.nanxu.payment.channel.model.PaymentMethod;
import net.nanxu.payment.money.Money;
import run.halo.app.extension.AbstractExtension;

/**
 * Order.
 *
 * @author: P
 **/
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
// @GVK(kind = "Order",
//     group = "order.payment.plugin.nanxu.net",
//     version = "v1alpha1",
//     singular = "order",
//     plural = "orders")
public class Order extends AbstractExtension {
    /**
     * 订单单号
     */
    @NotBlank
    private String orderNo;
    /**
     * 外部订单号
     */
    private String outTradeNo;
    /**
     * 订单标题
     */
    @NotBlank
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
    @NotNull
    private Money money;
    /**
     * 业务插件
     */
    @NotNull
    private BusinessRef business;
    /**
     * 支付通道
     */
    @NotNull
    private ChannelRef channel;
    /**
     * 账户信息
     */
    @NotNull
    private AccountRef account;
    /**
     * 支付用户
     */
    @NotNull
    private UserRef user;
    /**
     * 订单状态
     */
    @NotNull
    private OrderStatus orderStatus;
    /**
     * 支付状态
     */
    @NotNull
    private PayStatus payStatus;
    /**
     * 退款状态
     */
    @NotNull
    private RefundStatus refundStatus;
    /**
     * 创建时间
     */
    @NotNull
    private Instant createdAt;

    @Data
    public static class Product {
        /**
         * 商品编号
         */
        @NotBlank
        private String productNo;
        /**
         * 商品标题
         */
        @NotBlank
        private String title;
        /**
         * 商品描述
         */
        private String description;
        /**
         * 商品价格
         */
        @NotBlank
        private Money money;
        /**
         * 商品数量
         */
        @NotBlank
        private Long quantity;
        /**
         * 商品类型
         */
        @NotBlank
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

    @Data
    public static class AccountRef {
        @NotBlank
        private String name;

        public AccountRef() {
        }

        public AccountRef(String name) {
            this.name = name;
        }

    }

    @Data
    @Accessors(chain = true)
    public static class BusinessRef {

        /**
         * 业务名称
         */
        @NotBlank
        private String name;
        /**
         * 在收银台中需要返回的地址
         */
        @NotBlank
        private String returnUrl;
        /**
         * 扩展数据
         */
        private Map<String, Object> extra;

        public BusinessRef addExtra(String key, Object value) {
            if (extra == null) {
                extra = new HashMap<>();
            } else {
                extra.put(key, value);
            }
            return this;
        }

    }

    @Data
    @Accessors(chain = true)
    public static class UserRef {

        /**
         * 内部用户名称
         */
        @NotBlank
        private String name;
        /**
         * 用户邮箱
         */
        private String email;
        /**
         * 外部用户ID(一般是第三方支付通道提供的用户ID，如：微信的openId)
         */
        private String outerId;
    }

    public enum OrderStatus {
        /**
         * 等待交易
         */
        WAITING,
        /**
         * 交易成功
         */
        SUCCESS,
        /**
         * 交易关闭，例如点击收货、关闭订单、取消订单、订单超时、全部退款
         */
        CLOSED
    }

    public enum PayStatus {
        /**
         * 未支付
         */
        UNPAID,
        /**
         * 支付成功
         */
        PAID
    }

    @Data
    @Accessors(chain = true)
    public static class ChannelRef {

        /**
         * 支付通道名称
         */
        private String name;
        /**
         * 支付方式
         */
        private PaymentMethod method;
        /**
         * 通知地址
         */
        @NotBlank
        private String notifyUrl;
        /**
         * 扩展数据
         */
        private Map<String, Object> extra;

        public ChannelRef() {
        }

        public ChannelRef(String name) {
            this.name = name;
        }

        public ChannelRef addExtra(String key, Object value) {
            if (extra == null) {
                extra = new HashMap<>();
            } else {
                extra.put(key, value);
            }
            return this;
        }

        public static ChannelRef of(String name) {
            return new ChannelRef().setName(name);
        }

        public static ChannelRef of(String name, PaymentMethod method) {
            return new ChannelRef().setName(name).setMethod(method);
        }

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

    public static Order createOrder() {
        return new Order()
            .setPayStatus(Order.PayStatus.UNPAID)
            .setRefundStatus(Order.RefundStatus.UNREFUNDED)
            .setCreatedAt(Instant.now())
            ;
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
