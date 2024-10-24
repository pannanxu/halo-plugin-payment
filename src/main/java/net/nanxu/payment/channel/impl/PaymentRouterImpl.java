package net.nanxu.payment.channel.impl;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import net.nanxu.payment.channel.IPayment;
import net.nanxu.payment.channel.IPaymentRouter;
import net.nanxu.payment.channel.PaymentRegistry;
import net.nanxu.payment.router.AbstractRouter;
import net.nanxu.payment.router.RouterFilterRequest;
import net.nanxu.payment.router.RouterRuleConfig;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * PaymentRouterImpl.
 *
 * @author: P
 **/
@RequiredArgsConstructor
@Component
public class PaymentRouterImpl extends AbstractRouter<List<IPayment>>
    implements IPaymentRouter {

    private final PaymentRegistry registry;

    @Override
    protected Mono<List<IPayment>> filter(RouterFilterRequest request, List<String> channels,
        Map<String, Long> countMap) {
        List<IPayment> sorted = countMap.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .map(registry::get)
            .toList();
        return Mono.just(sorted);
    }

    @Override
    protected List<RouterRuleConfig.Rule> getRules(RouterRuleConfig config) {
        return config.getChannels();
    }
}
