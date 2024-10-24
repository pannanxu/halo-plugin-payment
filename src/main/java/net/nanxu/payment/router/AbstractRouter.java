package net.nanxu.payment.router;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

/**
 * AbstractRouter.
 *
 * @author: P
 **/
public abstract class AbstractRouter<T> implements IRouter<T> {
    private final ExpressionParser parser = new SpelExpressionParser();
    
    @Override
    public Mono<T> filter(RouterFilterRequest request) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setRootObject(request);
        return fetchRuleConfig()
            .flatMap(rules -> {
                List<String> list = new ArrayList<>();
                rules.stream()
                    .filter(rule -> StringUtils.isNotBlank(rule.getExpression()))
                    .filter(rule -> !CollectionUtils.isEmpty(rule.getValues()))
                    .filter(rule -> {
                        Expression complexExp = parser.parseExpression(rule.getExpression());
                        Boolean complexResult = complexExp.getValue(context, Boolean.class);
                        return null != complexResult && complexResult;
                    })
                    .forEach(e -> list.addAll(e.getValues()));

                // 计算list每个值出现的次数
                Map<String, Long> countMap = list.stream()
                    .collect(Collectors.groupingBy(e -> e, Collectors.counting()));

                return filter(request, list, countMap);
            });
    }

    protected abstract Mono<T> filter(RouterFilterRequest request, List<String> channels,
        Map<String, Long> countMap);

    protected abstract List<RouterRuleConfig.Rule> getRules(RouterRuleConfig config);

    public Mono<List<RouterRuleConfig.Rule>> fetchRuleConfig() {
        // TODO store in database.
        RouterRuleConfig config = new RouterRuleConfig();
        config.setAccounts(List.of(
            new RouterRuleConfig.Rule("userAgent.contains('alipayclient')", List.of("alipay")),
            new RouterRuleConfig.Rule("userAgent.contains('micromessenger')", List.of("wechat")),
            new RouterRuleConfig.Rule("isPc()", List.of("alipay", "wechat", "google")),
            new RouterRuleConfig.Rule("isWap()", List.of("alipay", "wechat", "google"))
        ));
        config.setChannels(List.of(
            new RouterRuleConfig.Rule("userAgent.contains('alipayclient')", List.of("alipay")),
            new RouterRuleConfig.Rule("userAgent.contains('micromessenger')", List.of("wechat")),
            new RouterRuleConfig.Rule("isPc()", List.of("alipay", "wechat", "google")),
            new RouterRuleConfig.Rule("isWap()", List.of("alipay", "wechat", "google"))
        ));
        return Mono.just(getRules(config));
    }
}
