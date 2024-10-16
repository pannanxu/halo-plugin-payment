package net.nanxu.payment.service.impl;

import java.time.Duration;
import net.nanxu.payment.exception.PaymentException;
import net.nanxu.payment.infra.model.CallbackRequest;
import net.nanxu.payment.infra.model.CallbackResult;
import net.nanxu.payment.infra.model.Order;
import net.nanxu.payment.registry.NotificationRegistry;
import net.nanxu.payment.registry.PaymentRegistry;
import net.nanxu.payment.service.AccountService;
import net.nanxu.payment.service.CallbackService;
import net.nanxu.payment.service.OrderService;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import reactor.util.retry.Retry;

/**
 * PaymentCallbackService.
 *
 * @author: P
 **/
public class CallbackServiceImpl implements CallbackService {

    public static final String INTERNAL = "hello";

    private final NotificationRegistry notificationRegistry;
    private final PaymentRegistry paymentRegistry;
    private final OrderService orderService;
    private final AccountService accountService;

    public CallbackServiceImpl(NotificationRegistry notificationRegistry,
        PaymentRegistry paymentRegistry, OrderService orderService,
        AccountService accountService) {
        this.notificationRegistry = notificationRegistry;
        this.paymentRegistry = paymentRegistry;
        this.orderService = orderService;
        this.accountService = accountService;
    }

    @Override
    public Mono<Object> callback(String channel, String orderNo, ServerRequest request) {
        // 验证系统配置的内部路径是否正确
        return validatePath(request)
            // 订单状态验证
            .flatMap(e -> handleBusinessLogic(channel, orderNo, request))
            // 通知业务插件
            .flatMap(e -> handleNotify(e.getT1(), e.getT2()))
            // 第三方插件返回给支付商的内容
            .map(CallbackResult::getRender);
    }

    private Mono<Boolean> validatePath(ServerRequest request) {
        return Mono.defer(() -> {
            String internal = request.pathVariable("internal");
            // TODO 系统配置的内部路径配置化
            if (INTERNAL.equals(internal)) {
                return Mono.just(true);
            }
            return Mono.error(new PaymentException("内部路径错误"));
        });
    }

    private Mono<Tuple2<Order, CallbackResult>> handleBusinessLogic(String channel, String orderNo,
        ServerRequest request) {
        return Mono.defer(() -> orderService.getOrder(orderNo)
                .flatMap(order -> {
                    if (Order.PayStatus.PAYING.equals(order.getPayStatus())) {
                        return Mono.just(order);
                    }
                    return Mono.error(new PaymentException("订单状态异常"));
                })
                // 支付插件处理
                .flatMap(order -> handlePayment(order, request))
                // 更新订单处理
                .flatMap(e -> {
                    CallbackResult result = e.getT2();
                    Order order = e.getT1();
                    if (result.getSuccess()) { // 第三方支付插件反馈支付成功
                        // 更新订单状态为支付成功
                        order.setPayStatus(Order.PayStatus.PAID);
                    }
                    return orderService.updateOrder(order).map(e1 -> Tuples.of(order, result));
                }))
            .retryWhen(Retry.backoff(5, Duration.ofMillis(100))
                .filter(OptimisticLockingFailureException.class::isInstance));
    }

    private Mono<Tuple2<Order, CallbackResult>> handlePayment(Order order, ServerRequest request) {
        return Mono.defer(() -> {
            String channel = order.getPayment().getName();
            return accountService.getAccount(order.getAccount().getName())
                .flatMap(account -> paymentRegistry.get(channel)
                    .getCallback().callback(CallbackRequest.builder().request(request)
                        .channel(channel).orderNo(order.getOrderNo())
                        .account(account).order(order).build())
                    .map(e -> Tuples.of(order, e)));
        });
    }

    private Mono<CallbackResult> handleNotify(Order order, CallbackResult result) {
        return Mono.defer(
                () -> notificationRegistry.getNotification(order.getBusiness().getName()).notify(order)
            )
            .mapNotNull(e -> e ? result : null)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                .filter(RuntimeException.class::isInstance));
    }
}
