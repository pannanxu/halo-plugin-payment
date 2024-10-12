package net.nanxu.payment.security.impl;

import lombok.extern.slf4j.Slf4j;
import net.nanxu.payment.security.SecurityModule;
import net.nanxu.payment.security.SecurityModuleContext;
import reactor.core.publisher.Mono;

/**
 * Encryptor.
 *
 * @author: P
 **/
@Slf4j
public class Encryptor implements SecurityModule {
    @Override
    public Mono<Type> check(SecurityModuleContext context) {
        log.info("Encryptor");
        return Mono.just(Type.Allow);
    }
}
