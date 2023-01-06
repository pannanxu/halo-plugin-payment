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

    created("created", "已创建"),
    process("process", "处理中"),
    successful("successful", "支付成功"),
    canceled("canceled", "支付取消"),
    ;

    private final String code;
    private final String name;

    public boolean check(String code) {
        return this.code.equals(code);
    }

}
