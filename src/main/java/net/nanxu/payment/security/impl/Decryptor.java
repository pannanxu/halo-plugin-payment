package net.nanxu.payment.security.impl;

import lombok.extern.slf4j.Slf4j;
import net.nanxu.payment.security.PaymentBeforeSecurityModule;
import net.nanxu.payment.security.SecurityModuleContext;
import reactor.core.publisher.Mono;

/**
 * Decryptor.
 *
 * @author: P
 **/
@Slf4j
public class Decryptor implements PaymentBeforeSecurityModule {
    @Override
    public Mono<Type> check(SecurityModuleContext context) {
        log.info("Decryptor");
        return Mono.just(Type.Allow);
    }
}
