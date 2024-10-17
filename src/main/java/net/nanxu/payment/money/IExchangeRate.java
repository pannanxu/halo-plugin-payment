package net.nanxu.payment.money;

import org.pf4j.ExtensionPoint;
import reactor.core.publisher.Mono;

/**
 * IExchangeRate.
 *
 * @author: P
 **/
public interface IExchangeRate extends ExtensionPoint {

    String getName();

    Mono<ExchangeRateResult> convert(Currency base);

}
