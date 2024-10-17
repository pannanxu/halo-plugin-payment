package net.nanxu.payment.setting;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.nanxu.payment.account.Account;
import run.halo.app.extension.AbstractExtension;

/**
 * PaymentSetting.
 *
 * @author: P
 **/
public class PaymentSetting {

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Basic extends AbstractExtension {
        /**
         * 内部的地址，可以是一个uuid或自定义字符串，避免回调接口被恶意调用
         */
        private String internal;

    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class AccountSetting extends AbstractExtension {
        /**
         * 是否启用
         */
        private Boolean enabled;
        /**
         * 账户信息
         */
        private Account account;
    }

}
