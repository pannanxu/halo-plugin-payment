package net.nanxu.payment.service.impl;

import java.time.Duration;
import net.nanxu.payment.infra.model.CallbackRequest;
import net.nanxu.payment.infra.model.CallbackResult;
import net.nanxu.payment.infra.model.Order;
import net.nanxu.payment.registry.BusinessRegistry;
import net.nanxu.payment.registry.PaymentRegistry;
import net.nanxu.payment.service.OrderService;
import net.nanxu.payment.service.CallbackService;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;
import reactor.util.retry.Retry;

/**
 * PaymentCallbackService.
 *
 * @author: P
 **/
public class CallbackServiceImpl implements CallbackService {

    private final BusinessRegistry businessRegistry;
    private final PaymentRegistry paymentRegistry;
    private final OrderService orderService;

    public CallbackServiceImpl(BusinessRegistry businessRegistry,
        PaymentRegistry paymentRegistry, OrderService orderService) {
        this.businessRegistry = businessRegistry;
        this.paymentRegistry = paymentRegistry;
        this.orderService = orderService;
    }

    public Mono<Object> callback(String channel, String orderNo, ServerRequest request) {
        return orderService.getOrder(orderNo)
            .flatMap(order -> {
                if (Order.PayStatus.PAYING.equals(order.getPayStatus())) {
                    return Mono.just(order);
                }
                return Mono.error(new RuntimeException("订单状态异常"));
            })
            // 支付插件处理
            .flatMap(order -> paymentRegistry.get(channel)
                .getCallback()
                .callback(CallbackRequest.builder()
                    .request(request)
                    .channel(channel)
                    .orderNo(orderNo)
                    .order(order)
                    .build())
                .map(e -> Tuples.of(order, e)))
            // 更新订单处理
            .flatMap(e -> {
                CallbackResult result = e.getT2();
                Order order = e.getT1();
                if (result.getSuccess()) { // 第三方支付插件反馈支付成功
                    // 更新订单状态为支付成功
                    order.setPayStatus(Order.PayStatus.PAID);
                }
                return orderService.updateOrder(order).map(e1 -> Tuples.of(order, result));
            })
            // 通知业务插件
            .flatMap(e -> businessRegistry.getNotification(e.getT1().getBusiness().getName())
                .notify(e.getT1())
                .mapNotNull(result -> result ? e.getT2() : null)
                // 发生AppException时重试
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                    .filter(RuntimeException.class::isInstance))
            )
            // 第三方插件返回给支付商的内容
            .map(CallbackResult::getRender);
    }
}
