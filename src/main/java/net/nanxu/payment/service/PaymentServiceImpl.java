package net.nanxu.payment.service;

import net.nanxu.payment.infra.model.PaymentRequest;
import net.nanxu.payment.infra.model.PaymentResult;
import net.nanxu.payment.infra.model.QueryRequest;
import net.nanxu.payment.infra.model.QueryResult;
import net.nanxu.payment.infra.model.RefundRequest;
import net.nanxu.payment.infra.model.RefundResult;
import net.nanxu.payment.registry.BusinessRegistry;
import net.nanxu.payment.security.PaymentBeforeSecurityModule;
import net.nanxu.payment.security.SecurityModule;
import net.nanxu.payment.security.SecurityModuleContext;
import net.nanxu.payment.security.SecurityRegistry;
import reactor.core.publisher.Mono;

/**
 * PaymentServiceImpl.
 *
 * @author: P
 **/
public class PaymentServiceImpl implements PaymentService {

    private final SecurityRegistry security;
    private final PaymentCallbackService callback;
    private final OrderService orderService;

    public PaymentServiceImpl( SecurityRegistry security, BusinessRegistry businessRegistry, OrderService orderService) {
        this.security = security;
        this.orderService = orderService;
        this.callback = new PaymentCallbackService(businessRegistry, null, orderService);
    }

    @Override
    public Mono<PaymentResult> pay(PaymentRequest request) {
        return security.getModules(PaymentBeforeSecurityModule.class)
            .flatMap(module -> module.check(new SecurityModuleContext(request)))
            .collectList()
            .flatMap(types -> types.contains(SecurityModule.Type.Reject)
                ? Mono.error(new RuntimeException("Reject"))
                : Mono.just("Success"))
            .flatMap(order -> null);
    }

    @Override
    public Mono<QueryResult> query(QueryRequest request) {
        return null;
    }

    @Override
    public Mono<RefundResult> refund(RefundRequest request) {
        return null;
    }

    @Override
    public Mono<RefundResult> cancel(RefundRequest request) {
        return null;
    }
}
