package net.nanxu.payment.money;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.extensionpoint.ExtensionGetter;

/**
 * SimpleExchangeRateConverter.
 *
 * @author: P
 **/
@RequiredArgsConstructor
@Component
public class SimpleExchangeRateConverter implements ExchangeRateConverter {

    private final ExtensionGetter extensionGetter;

    @Override
    public Mono<BigDecimal> convert(Money money) {
        return resolve(money.getCurrency())
            .map(e -> {
                BigDecimal rate = e.getRates().get(money.getRate().getTarget().getAlphaCode());
                return money.getAmount().multiply(rate);
            });
    }

    public Mono<ExchangeRateResult> resolve(CurrencyUnit base) {
        return new SimpleExchangeRateProvider().convert(base);
    }
}
