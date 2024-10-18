package net.nanxu.payment.setting;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.nanxu.payment.account.Account;
import net.nanxu.payment.money.CurrencyUnit;
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

        /**
         * 基础汇率转换货币
         */
        private CurrencyUnit currency;

    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Money extends AbstractExtension {

        /**
         * 基础汇率转换货币
         */
        private CurrencyUnit currency;
        /**
         * 使用的汇率转换器
         */
        private String useExchangeRateConvert;

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
