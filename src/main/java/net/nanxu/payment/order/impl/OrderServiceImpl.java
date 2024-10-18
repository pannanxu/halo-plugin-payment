package net.nanxu.payment.order.impl;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.nanxu.payment.generator.OrderNoProvider;
import net.nanxu.payment.generator.PaymentLinkGenerator;
import net.nanxu.payment.money.Money;
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
    public Order createSimpleOrder() {
        Order order = Order.createOrder();
        return order
            .setOrderNo(null/*OrderNoGenerator.simple.generate(order)*/)
            .setSubject("测试订单")
            .setDescription("测试订单描述")
            .setProducts(List.of())
            .setMoney(Money.ofCNY(new BigDecimal("100.00")))
            // .setPayment(Ref.of("WeChat")) // 在收银台由用户自行选择后再写入
            // .setUser(Ref.of("user-1"))
            ;
    }

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
