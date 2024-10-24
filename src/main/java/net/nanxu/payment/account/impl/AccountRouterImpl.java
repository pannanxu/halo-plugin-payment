package net.nanxu.payment.account.impl;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import net.nanxu.payment.account.AccountService;
import net.nanxu.payment.account.IAccount;
import net.nanxu.payment.account.IAccountRouter;
import net.nanxu.payment.router.AbstractRouter;
import net.nanxu.payment.router.RouterFilterRequest;
import net.nanxu.payment.router.RouterRuleConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 账户路由器，获取权重最高的账户.
 *
 * @author: P
 **/
@Component
@RequiredArgsConstructor
public class AccountRouterImpl extends AbstractRouter<IAccount>
    implements IAccountRouter {

    private final AccountService accountService;

    @Override
    protected Mono<IAccount> filter(RouterFilterRequest request, List<String> channels, Map<String, Long> countMap) {
        String maxKey = countMap.entrySet()
            .stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
        if (StringUtils.isBlank(maxKey)) {
            // 返回默认的账户
            return accountService.getDefaultAccount(request.getOrder().getChannel().getName());
        }
        return accountService.getAccount(maxKey);
    }

    @Override
    protected List<RouterRuleConfig.Rule> getRules(RouterRuleConfig config) {
        return config.getAccounts();
    }
}
