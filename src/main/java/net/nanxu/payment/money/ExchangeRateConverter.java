package net.nanxu.payment.money;

import java.math.BigDecimal;
import reactor.core.publisher.Mono;

/**
 * 汇率转换器.
 *
 * @author: P
 **/
public interface ExchangeRateConverter {

    Mono<BigDecimal> convert(Money money);

}
