package net.nanxu.payment.order;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.nanxu.payment.channel.model.PaymentMethod;
import net.nanxu.payment.money.Money;
import org.apache.commons.lang3.StringUtils;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.Ref;

/**
 * Order.
 *
 * @author: P
 **/
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
// @GVK(kind = "Order", group = "order.payment.plugin.nanxu.net", version = "v1alpha1", singular = 
//         "order", plural = "orders")
public class Order extends AbstractExtension {
    /**
     * 订单单号
     */
    private String orderNo;
    /**
     * 外部订单号
     */
    private String outTradeNo;
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
    private BusinessRef business;
    /**
     * 支付通道
     */
    private ChannelRef channel;
    /**
     * 账户信息
     */
    private AccountRef account;
    /**
     * 支付用户
     */
    private UserRef user;
    /**
     * 支付状态
     */
    private PayStatus payStatus;
    /**
     * 退款状态
     */
    private RefundStatus refundStatus;
    
    private Instant createdAt;

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

    @Data
    public static class AccountRef {
        public static final String MASTER = "_master";
        private String name;

        public AccountRef() {
        }

        public AccountRef(String name) {
            this.name = name;
        }

        public String getNameOrDefault(String channel) {
            return StringUtils.isNotBlank(name) ? name : (channel + MASTER);
        }
    }

    @Data
    @Accessors(chain = true)
    public static class BusinessRef {

        /**
         * 业务名称
         */
        private String name;
        /**
         * 在收银台中需要返回的地址
         */
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
        private String notifyUrl;
        /**
         * 扩展数据
         */
        private Map<String, Object> extra;

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
