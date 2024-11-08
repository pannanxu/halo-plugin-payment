package net.nanxu.payment.channel.impl;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import net.nanxu.payment.account.AccountService;
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
        return Mono.defer(() -> orderService.getOrder(request.getOrderNo())
                .switchIfEmpty(Mono.error(new PaymentException("订单不存在")))
                .filter(order -> Order.PayStatus.UNPAID.equals(order.getPayStatus()))
                .switchIfEmpty(Mono.error(new PaymentException("订单状态异常")))
                .doOnNext(request::setOrder)
                // 支付插件处理
                .flatMap(order -> handlePayment(order, request))
                // 更新订单处理
                .flatMap(e -> orderService.paidOrder(e.getT1().getOrderNo()).thenReturn(e.getT2())))
            .retryWhen(Retry.backoff(5, Duration.ofMillis(100))
                .filter(OptimisticLockingFailureException.class::isInstance))
            // 第三方插件返回给支付商的内容
            .map(CallbackResult::getRender);
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

}
