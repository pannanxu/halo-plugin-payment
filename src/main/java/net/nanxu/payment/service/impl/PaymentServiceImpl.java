package net.nanxu.payment.service.impl;

import net.nanxu.payment.infra.model.Order;
import net.nanxu.payment.infra.model.PaymentRequest;
import net.nanxu.payment.infra.model.PaymentResult;
import net.nanxu.payment.infra.model.QueryRequest;
import net.nanxu.payment.infra.model.QueryResult;
import net.nanxu.payment.infra.model.RefundRequest;
import net.nanxu.payment.infra.model.RefundResult;
import net.nanxu.payment.registry.PaymentRegistry;
import net.nanxu.payment.security.PaymentBeforeSecurityModule;
import net.nanxu.payment.security.SecurityModule;
import net.nanxu.payment.security.SecurityModuleContext;
import net.nanxu.payment.security.SecurityRegistry;
import net.nanxu.payment.service.AccountService;
import net.nanxu.payment.service.OrderService;
import net.nanxu.payment.service.PaymentService;
import reactor.core.publisher.Mono;

/**
 * PaymentServiceImpl.
 *
 * @author: P
 **/
public class PaymentServiceImpl implements PaymentService {

    private final SecurityRegistry security;
    private final PaymentRegistry paymentRegistry;
    private final OrderService orderService;
    private final AccountService accountService;

    public PaymentServiceImpl(SecurityRegistry security, PaymentRegistry paymentRegistry,
        OrderService orderService,
        AccountService accountService) {
        this.security = security;
        this.paymentRegistry = paymentRegistry;
        this.orderService = orderService;
        this.accountService = accountService;
    }

    @Override
    public Mono<PaymentResult> pay(PaymentRequest request) {
        return security.getModules(PaymentBeforeSecurityModule.class)
            .flatMap(module -> module.check(new SecurityModuleContext(request)))
            .collectList()
            .flatMap(types -> types.contains(SecurityModule.Type.Reject)
                ? Mono.error(new RuntimeException("Reject"))
                : Mono.just("Success"))
            // 获取账户
            .flatMap((e) -> accountService.getAccount(request.getOrder().getAccount()
                .getNameOrDefault(request.getOrder().getPayment().getName())))
            // TODO 创建订单
            .flatMap(account -> {
                Order.AccountRef accountRef = new Order.AccountRef();
                accountRef.setName(account.getName());
                request.getOrder().setAccount(accountRef);
                request.setAccount(account);
                return orderService.createOrder(request.getOrder());
            })
            // 调用支付接口
            .flatMap(order -> paymentRegistry.get(order.getPayment().getName()).pay(request));
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
