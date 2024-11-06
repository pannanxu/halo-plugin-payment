package net.nanxu.payment.channel.impl;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import net.nanxu.payment.account.AccountService;
import net.nanxu.payment.business.BusinessRegistry;
import net.nanxu.payment.channel.CallbackService;
import net.nanxu.payment.channel.PaymentRegistry;
import net.nanxu.payment.channel.model.CallbackRequest;
import net.nanxu.payment.channel.model.CallbackResult;
import net.nanxu.payment.exception.PaymentException;
import net.nanxu.payment.order.Order;
import net.nanxu.payment.order.OrderService;
import net.nanxu.payment.setting.PaymentSettingService;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import reactor.util.retry.Retry;

/**
 * PaymentCallbackService.
 *
 * @author: P
 **/
@Service
@RequiredArgsConstructor
public class CallbackServiceImpl implements CallbackService {

    private final BusinessRegistry businessRegistry;
    private final PaymentRegistry paymentRegistry;
    private final OrderService orderService;
    private final AccountService accountService;
    private final PaymentSettingService settingService;

    @Override
    public Mono<Boolean> validateInternal(String internal) {
        return settingService.getBasicSetting().map(basic -> basic.getInternal().equals(internal));
    }

    @Override
    public Mono<Object> callback(CallbackRequest request) {
        // 订单状态验证
        return handleBusinessLogic(request)
            // 通知业务插件 TODO 后续优化方向：将业务插件通知放在异步队列中处理
            .flatMap(e -> handleNotify(e.getT1(), e.getT2()))
            // 第三方插件返回给支付商的内容
            .map(CallbackResult::getRender);
    }

    private Mono<Tuple2<Order, CallbackResult>> handleBusinessLogic(CallbackRequest request) {
        return Mono.defer(() -> orderService.getOrder(request.getOrderNo())
                .switchIfEmpty(Mono.error(new PaymentException("订单不存在")))
                .filter(order -> Order.PayStatus.UNPAID.equals(order.getPayStatus()))
                .switchIfEmpty(Mono.error(new PaymentException("订单状态异常")))
                .doOnNext(request::setOrder)
                // 支付插件处理
                .flatMap(order -> handlePayment(order, request))
                .doOnNext(e -> {
                    if (e.getT2().getSuccess()) { // 第三方支付插件反馈支付成功
                        // 更新订单状态为支付成功
                        e.getT1().setPayStatus(Order.PayStatus.PAID);
                    }
                })
                // 更新订单处理
                .flatMap(e -> orderService.updateOrder(e.getT1()).thenReturn(e)))
            .retryWhen(Retry.backoff(5, Duration.ofMillis(100))
                .filter(OptimisticLockingFailureException.class::isInstance));
    }

    private Mono<Tuple2<Order, CallbackResult>> handlePayment(Order order,
        CallbackRequest request) {
        return Mono.defer(() -> {
            String channel = order.getChannel().getName();
            return accountService.getAccount(order.getAccount().getName())
                .flatMap(account -> paymentRegistry.get(channel)
                    .getCallback().callback(request)
                    .map(e -> Tuples.of(order, e)));
        });
    }

    private Mono<CallbackResult> handleNotify(Order order, CallbackResult result) {
        return Mono.defer(() -> businessRegistry.getBusiness(order.getBusiness().getName())
                .notify(order))
            .mapNotNull(e -> e ? result : null)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                .filter(RuntimeException.class::isInstance));
    }
}
