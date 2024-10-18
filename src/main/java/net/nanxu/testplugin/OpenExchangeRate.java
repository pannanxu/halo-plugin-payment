package net.nanxu.testplugin;

import net.nanxu.payment.money.CurrencyUnit;
import net.nanxu.payment.money.ExchangeRateResult;
import net.nanxu.payment.money.IExchangeRate;
import reactor.core.publisher.Mono;

/**
 * use https://openexchangerates.org
 * 
 * @author: P
 **/
public class OpenExchangeRate implements IExchangeRate {

    @Override
    public String getName() {
        return "OpenExchangeRates";
    }

    @Override
    public Mono<ExchangeRateResult> convert(CurrencyUnit base) {
        return null;
    }
}
