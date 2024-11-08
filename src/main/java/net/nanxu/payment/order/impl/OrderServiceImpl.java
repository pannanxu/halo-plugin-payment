package net.nanxu.payment.order.impl;

import lombok.RequiredArgsConstructor;
import net.nanxu.payment.exception.OrderException;
import net.nanxu.payment.generator.OrderNoProvider;
import net.nanxu.payment.generator.PaymentLinkGenerator;
import net.nanxu.payment.order.Order;
import net.nanxu.payment.order.OrderService;
import net.nanxu.payment.order.event.OrderCanceledEvent;
import net.nanxu.payment.order.event.OrderClosedEvent;
import net.nanxu.payment.order.event.OrderCreatedEvent;
import net.nanxu.payment.order.event.OrderPaidEvent;
import net.nanxu.payment.setting.PaymentSettingService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ReactiveExtensionClient;

/**
 * OrderService.
 *
 * @author: P
 **/
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderNoProvider orderNoProvider;
    private final PaymentLinkGenerator paymentLinkGenerator;
    private final PaymentSettingService settingService;
    private final ReactiveExtensionClient client;
    private final ApplicationEventPublisher publisher;

    @Override
    public Mono<Order> getOrder(String orderNo) {
        return client.get(Order.class, orderNo);
    }

    @Override
    public Mono<Order> createOrder(Order request) {
        return orderNoProvider.generate()
            .map(e -> {
                request.setOrderNo(e);
                return request;
            })
            .flatMap(order -> settingService.getBasicSetting().map(basic -> {
                if (null == order.getChannel()) {
                    order.setChannel(new Order.ChannelRef());
                }
                String callbackUrl = paymentLinkGenerator.callbackUrl(basic.getInternal(),
                    order.getOrderNo(),
                    order.getChannel().getName());
                order.setChannel(order.getChannel().setNotifyUrl(callbackUrl));
                return order;
            }))
            .flatMap(client::create)
            .doOnNext(e -> publisher.publishEvent(new OrderCreatedEvent(this, e)));
    }

    @Override
    public Mono<Order> paidOrder(String orderNo) {
        return getOrder(orderNo)
            .flatMap(e -> {
                if (!e.getPayStatus().equals(Order.PayStatus.UNPAID)) {
                    return Mono.error(new OrderException("此订单不允许修改此状态"));
                }
                e.setPayStatus(Order.PayStatus.PAID);
                e.setOrderStatus(Order.OrderStatus.SUCCESS);
                return Mono.just(e);
            })
            .flatMap(client::update)
            .doOnNext(e -> publisher.publishEvent(new OrderPaidEvent(this, e)));
    }

    @Override
    public Mono<Order> cancelOrder(String orderNo) {
        return getOrder(orderNo)
            .flatMap(e -> {
                if (e.getPayStatus().equals(Order.PayStatus.PAID)) {
                    return Mono.error(new OrderException("订单已支付"));
                }
                if (e.getOrderStatus().equals(Order.OrderStatus.CLOSED)) {
                    return Mono.error(new OrderException("此订单不允许修改此状态"));
                }
                e.setOrderStatus(Order.OrderStatus.CLOSED);
                return Mono.just(e);
            })
            .flatMap(client::update)
            .doOnNext(e -> publisher.publishEvent(new OrderCanceledEvent(this, e)));
    }

    @Override
    public Mono<Order> closeOrder(String orderNo) {
        return getOrder(orderNo)
            .flatMap(e -> {
                if (e.getOrderStatus().equals(Order.OrderStatus.CLOSED)) {
                    return Mono.error(new OrderException("此订单不允许修改此状态"));
                }
                e.setOrderStatus(Order.OrderStatus.CLOSED);
                return Mono.just(e);
            })
            .flatMap(client::update)
            .doOnNext(e -> publisher.publishEvent(new OrderClosedEvent(this, e)));
    }

}
