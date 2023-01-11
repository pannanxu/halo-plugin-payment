package io.mvvm.halo.plugins.payment.sdk.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * PaymentStatus.
 *
 * @author: pan
 **/
@AllArgsConstructor
@Getter
public enum PaymentStatus {
    /**
     * 订单已创建、等待用户付款。
     * <p>
     * 注意：部分第三方支付在创建订单时并未真实创建，所以查询订单详情是查询不到的
     */
    created("created", "订单已创建"),
    /**
     * 用户已经支付成功
     */
    payment_successful("payment_successful", "支付成功"),
    /**
     * 退款订单已创建、等待第三方支付处理退款
     */
    refund_processing("refund_processing", "退款处理中"),
    /**
     * 退款已成功，第三方支付已经把钱退给用户
     */
    refund_successful("refund_successful", "退款成功"),
    /**
     * 订单处于已关闭。取消、退款、超时等都是已关闭
     */
    closed("closed", "订单已关闭"),
    ;

    private final String code;
    private final String name;

    public boolean check(String code) {
        return this.code.equals(code);
    }

}
