package net.nanxu.payment.service;

import net.nanxu.payment.infra.model.CallbackRequest;
import net.nanxu.payment.infra.model.CallbackResult;
import net.nanxu.payment.infra.model.Order;
import net.nanxu.payment.registry.BusinessRegistry;
import net.nanxu.payment.registry.PaymentRegistry;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

/**
 * PaymentCallbackService.
 *
 * @author: P
 **/
public class PaymentCallbackService {

    private final BusinessRegistry businessRegistry;
    private final PaymentRegistry paymentRegistry;
    private final OrderService orderService;

    public PaymentCallbackService(BusinessRegistry businessRegistry,
        PaymentRegistry paymentRegistry, OrderService orderService) {
        this.businessRegistry = businessRegistry;
        this.paymentRegistry = paymentRegistry;
        this.orderService = orderService;
    }

    public Mono<CallbackResult> payCallback(CallbackRequest request) {

        return orderService.getOrder(request.getOrderNo())
            .flatMap(order -> {
                if (Order.PayStatus.PAYING.equals(order.getPayStatus())) {
                    return Mono.just(order);
                }
                return Mono.error(new RuntimeException("订单状态异常"));
            })
            // 支付插件处理
            .flatMap(order -> {
                request.setOrder(order);
                return paymentRegistry.get(request.getPayment())
                    .getCallback()
                    .payCallback(request)
                    .map(e -> Tuples.of(order, e));
            })
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
                .mapNotNull(result -> {
                    if (result) {
                        return e.getT2();
                    } else {
                        return null;
                    }
                }));
    }
}
