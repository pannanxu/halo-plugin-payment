package net.nanxu.payment.money;

import reactor.core.publisher.Mono;

/**
 * ExchangeRateConverter.
 *
 * @author: P
 **/
public interface ExchangeRateConverter {

    Mono<Money> convert(Money money, Currency target);

}
