package net.nanxu.payment.money;

import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 默认不做汇率计算.
 *
 * @author: P
 **/
public class SimpleExchangeRate implements IExchangeRate {
    @Override
    public String getName() {
        return "Simple";
    }

    @Override
    public Mono<ExchangeRateResult> convert(Currency base) {
        ExchangeRateResult result = new ExchangeRateResult();
        Map<String, BigDecimal> rates = new HashMap<>();
        for (Currency value : Currency.values()) {
            rates.put(value.getAlphaCode(), BigDecimal.ONE);
        }
        result.setRates(rates);
        return Mono.justOrEmpty(result);
    }
}
