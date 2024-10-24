package net.nanxu.payment.router;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RouterRuleConfig.
 *
 * @author: P
 **/
@Data
public class RouterRuleConfig {

    // 账户规则列表
    private List<Rule> accounts;


    // 通道规则列表
    private List<Rule> channels;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Rule {
        // 表达式
        private String expression;
        // 支付或账户渠道列表
        private List<String> values;
    }
}
