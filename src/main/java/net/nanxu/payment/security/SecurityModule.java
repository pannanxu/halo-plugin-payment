package net.nanxu.payment.security;

import reactor.core.publisher.Mono;

/**
 * SecurityModule.
 *
 * @author: P
 **/
public interface SecurityModule {

    Class<PaymentAfterSecurityModule> PAYMENT_AFTER = PaymentAfterSecurityModule.class;

    Class<PaymentBeforeSecurityModule> PAYMENT_BEFORE = PaymentBeforeSecurityModule.class;

    Mono<Type> check(SecurityModuleContext context);

    enum Type {
        // 放行
        Allow,
        // 拒绝
        Reject
    }
}
