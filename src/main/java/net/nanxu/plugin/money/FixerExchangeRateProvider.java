package net.nanxu.plugin.money;

import net.nanxu.payment.money.ExchangeRateProvider;
import net.nanxu.payment.money.ExchangeRateResult;
import reactor.core.publisher.Mono;

/**
 * use https://fixer.io/
 *
 * @author: P
 **/
public class FixerExchangeRateProvider implements ExchangeRateProvider {
    @Override
    public String getName() {
        return "Fixer";
    }

    @Override
    public Mono<ExchangeRateResult> convert(String base) {
        return null;
    }
}
