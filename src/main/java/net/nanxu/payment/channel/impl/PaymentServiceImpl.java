package net.nanxu.payment.channel.impl;

import lombok.RequiredArgsConstructor;
import net.nanxu.payment.account.AccountService;
import net.nanxu.payment.account.IAccount;
import net.nanxu.payment.account.IAccountRouter;
import net.nanxu.payment.channel.PaymentRegistry;
import net.nanxu.payment.channel.PaymentService;
import net.nanxu.payment.channel.model.PayRequest;
import net.nanxu.payment.channel.model.PaymentRequest;
import net.nanxu.payment.channel.model.PaymentResult;
import net.nanxu.payment.channel.model.QueryRequest;
import net.nanxu.payment.channel.model.QueryResult;
import net.nanxu.payment.channel.model.RefundRequest;
import net.nanxu.payment.channel.model.RefundResult;
import net.nanxu.payment.infra.ProtocolPacket;
import net.nanxu.payment.order.Order;
import net.nanxu.payment.order.OrderService;
import net.nanxu.payment.router.RouterFilterRequest;
import net.nanxu.payment.security.PaymentBeforeSecurityModule;
import net.nanxu.payment.security.SecurityModule;
import net.nanxu.payment.security.SecurityModuleContext;
import net.nanxu.payment.security.SecurityRegistry;
import net.nanxu.payment.utils.QrCodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

/**
 * PaymentServiceImpl.
 *
 * @author: P
 **/
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final SecurityRegistry security;
    private final PaymentRegistry paymentRegistry;
    private final OrderService orderService;
    private final AccountService accountService;
    private final IAccountRouter accountRouter;
    private final CacheManager cacheManager;

    @Override
    public Mono<PaymentResult> pay(PayRequest request) {
        return security.getModules(PaymentBeforeSecurityModule.class)
            .flatMap(module -> module.check(new SecurityModuleContext(request)))
            .collectList()
            .flatMap(types -> types.contains(SecurityModule.Type.Reject)
                ? Mono.error(new RuntimeException("Reject"))
                : Mono.just("Success"))
            // 创建订单
            .flatMap((e) -> orderService.getOrder(request.getOrderNo()))
            // 获取账户
            .flatMap((order) -> getAccount(order, request.getPacket()).map(
                account -> Tuples.of(order, account)))
            .flatMap(e -> {
                Cache cache =
                    cacheManager.getCache(buildPaymentResultCacheKey(e.getT1().getOrderNo()));
                if (null != cache) {
                    PaymentResult cacheResult =
                        cache.get(buildPaymentResultDataCacheKey(e.getT2()), PaymentResult.class);
                    if (null != cacheResult) {
                        return Mono.just(cacheResult);
                    }
                }
                PaymentRequest paymentRequest = new PaymentRequest();
                paymentRequest.setOrder(e.getT1());
                paymentRequest.setAccount(e.getT2());
                paymentRequest.setPacket(request.getPacket());
                return paymentRegistry.get(e.getT2().getChannel()).pay(paymentRequest)
                    .doOnNext(x -> {
                        if (null != cache) {
                            cache.put(buildPaymentResultDataCacheKey(e.getT2()), x);
                        }
                    });
            })
            // 后置处理
            .doOnNext(res -> {
                if (PaymentResult.Status.SUCCESS.equals(res.getStatus())
                    && PaymentResult.Type.QRCode.equals(res.getType())) {
                    if (!res.getContent().startsWith(QrCodeUtil.BASE64_PREFIX)) {
                        res.setContent(QrCodeUtil.encode(res.getContent()));
                    }
                }
            });
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

    private Mono<IAccount> getAccount(Order order, ProtocolPacket packet) {
        if (null != order.getAccount() && StringUtils.isNotBlank(order.getAccount().getName())) {
            return accountService.getAccount(order.getAccount().getName());
        }
        // 根据路由规则智能选择账户
        return accountRouter.filter(RouterFilterRequest.builder()
            .order(order)
            .packet(packet)
            .build());
    }

    private String buildPaymentResultDataCacheKey(IAccount account) {
        return String.join("_", account.getChannel(), account.getName());
    }

    private String buildPaymentResultCacheKey(String orderNo) {
        return String.join("_", "pay", orderNo);
    }
}
