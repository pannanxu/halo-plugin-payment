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

    created("created", "订单已创建"),

    payment_processing("payment_processing", "支付处理中"),
    payment_successful("payment_successful", "支付成功"),
    payment_canceled("payment_canceled", "支付取消"),

    refund_processing("refund_processing", "退款处理中"),
    refund_successful("refund_successful", "退款成功"),
    refund_canceled("refund_canceled", "退款取消"),

    cancel_successful("cancel_successful", "取消成功"),
    cancel_failed("cancel_failed", "取消失败"),

    closed("closed", "订单已关闭"),
    ;

    private final String code;
    private final String name;

    public boolean check(String code) {
        return this.code.equals(code);
    }

}
