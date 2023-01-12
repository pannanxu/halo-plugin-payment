package io.mvvm.halo.plugins.payment.sdk;

/**
 * 扩展参数key的枚举值.
 *
 * @author: pan
 **/
public interface ExpandConst {
    /**
     * pc 端支付成功后跳转页面
     */
    String returnUrl = "returnUrl";
    /**
     * 限流ip。可以是IP、用户名等任何字符串
     */
    String limitRuleKey = "ip_limit_rule";
    /**
     * 黑名单。可以是任意字符。与设置中保持一致即可.
     */
    String blackListRuleKey = "black_list_rule";
}
