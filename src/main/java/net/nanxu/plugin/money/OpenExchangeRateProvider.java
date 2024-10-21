package net.nanxu.plugin.money;

import net.nanxu.payment.money.CurrencyUnit;
import net.nanxu.payment.money.ExchangeRateProvider;
import net.nanxu.payment.money.ExchangeRateResult;
import reactor.core.publisher.Mono;

/**
 * use https://openexchangerates.org
 * 
 * @author: P
 **/
public class OpenExchangeRateProvider implements ExchangeRateProvider {

    @Override
    public String getName() {
        return "OpenExchangeRates";
    }

    @Override
    public Mono<ExchangeRateResult> convert(CurrencyUnit base) {
        return null;
    }
}
