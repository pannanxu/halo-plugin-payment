package net.nanxu.payment.security.impl;

import net.nanxu.payment.security.PaymentBeforeSecurityModule;
import net.nanxu.payment.security.SecurityModuleContext;
import reactor.core.publisher.Mono;

/**
 * FrequencyLimiter.
 *
 * @author: P
 **/
public class FrequencyLimiter implements PaymentBeforeSecurityModule {
    @Override
    public Mono<Type> check(SecurityModuleContext context) {
        return Mono.just(Type.Allow);
    }
}
