package net.nanxu.payment.money;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import reactor.core.publisher.Mono;

/**
 * 默认不做汇率计算.
 *
 * @author: P
 **/
public class SimpleExchangeRateProvider implements ExchangeRateProvider {
    @Override
    public String getName() {
        return "Simple";
    }

    @Override
    public Mono<ExchangeRateResult> convert(CurrencyUnit base) {
        ExchangeRateResult result = new ExchangeRateResult();
        Map<String, BigDecimal> rates = new HashMap<>();
        // for (Currency value : Currency.values()) {
        //     rates.put(value.getAlphaCode(), BigDecimal.ONE);
        // }
        result.setRates(rates);
        return Mono.justOrEmpty(result);
    }
}
