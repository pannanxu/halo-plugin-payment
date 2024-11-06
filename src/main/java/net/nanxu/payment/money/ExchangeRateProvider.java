package net.nanxu.payment.money;

import org.pf4j.ExtensionPoint;
import reactor.core.publisher.Mono;

/**
 * 汇率服务提供商.
 *
 * @author: P
 **/
public interface ExchangeRateProvider extends ExtensionPoint {

    String getName();

    Mono<ExchangeRateResult> convert(String base);

}
