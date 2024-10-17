package net.nanxu.payment.money;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.extensionpoint.ExtensionGetter;
import java.math.BigDecimal;

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
    public Mono<Money> convert(Money money, Currency target) {
        return resolve(money.getCurrency())
            .map(e -> {
                BigDecimal rate = e.getRates().get(target.getCode());
                money.setRate(new ExchangeRate(money.getCurrency(), target, rate));
                return money;
            });
    }

    public Mono<ExchangeRateResult> resolve(Currency base) {
        return new SimpleExchangeRate().convert(base);
    }
}
