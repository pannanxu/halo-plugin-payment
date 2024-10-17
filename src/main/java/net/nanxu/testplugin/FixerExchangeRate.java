package net.nanxu.testplugin;

import net.nanxu.payment.money.Currency;
import net.nanxu.payment.money.ExchangeRateResult;
import net.nanxu.payment.money.IExchangeRate;
import reactor.core.publisher.Mono;

/**
 * use https://fixer.io/
 *
 * @author: P
 **/
public class FixerExchangeRate implements IExchangeRate {
    @Override
    public String getName() {
        return "Fixer";
    }

    @Override
    public Mono<ExchangeRateResult> convert(Currency base) {
        return null;
    }
}
