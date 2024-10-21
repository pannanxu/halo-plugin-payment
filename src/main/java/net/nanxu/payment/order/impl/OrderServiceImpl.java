package net.nanxu.payment.order.impl;

import lombok.RequiredArgsConstructor;
import net.nanxu.payment.generator.OrderNoProvider;
import net.nanxu.payment.generator.PaymentLinkGenerator;
import net.nanxu.payment.order.Order;
import net.nanxu.payment.order.OrderService;
import net.nanxu.payment.setting.PaymentSettingService;
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
            .flatMap(client::create);
    }

    @Override
    public Mono<Order> updateOrder(Order order) {
        return client.update(order);
    }

}
