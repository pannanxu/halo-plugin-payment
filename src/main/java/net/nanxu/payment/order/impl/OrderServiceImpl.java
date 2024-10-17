package net.nanxu.payment.order.impl;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.nanxu.payment.acl.ExternalLinkCreator;
import net.nanxu.payment.generator.OrderNoProvider;
import net.nanxu.payment.model.Money;
import net.nanxu.payment.order.Order;
import net.nanxu.payment.order.OrderService;
import net.nanxu.payment.setting.PaymentSettingManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.Ref;

/**
 * OrderService.
 *
 * @author: P
 **/
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderNoProvider orderNoProvider;
    private final ExternalLinkCreator externalLinkCreator;
    private final PaymentSettingManager settingManager;
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
            .setUser(Ref.of("user-1"))
            .addExtra("simple", "第三方插件创建订单自行写入的扩展数据")
            ;
    }

    @Override
    public Mono<Order> getOrder(String orderNo) {
        return Mono.just(createSimpleOrder());
    }

    @Override
    public Mono<Order> createOrder(Order request) {
        return orderNoProvider.generate()
            .map(orderNo -> {
                Order order = Order.createOrder();
                return order
                    .setOrderNo(orderNo)
                    .setSubject(request.getSubject())
                    .setDescription(request.getDescription())
                    .setProducts(request.getProducts())
                    .setMoney(request.getMoney())
                    .setBusiness(request.getBusiness())
                    .setChannel(request.getChannel())
                    .setAccount(request.getAccount())
                    .setUser(request.getUser())
                    .setExtra(request.getExtra());
            })
            .flatMap(order -> settingManager.getBasicSetting().map(basic -> {
                if (null == order.getChannel()) {
                    order.setChannel(new Order.ChannelRef());
                }
                order.setChannel(order.getChannel().setNotifyUrl(
                    externalLinkCreator.callbackUrl(basic.getInternal(), order.getOrderNo(),
                        order.getChannel().getName())));
                return order;
            }))
            .flatMap(client::create);
    }

    @Override
    public Mono<Order> updateOrder(Order order) {
        return client.update(order);
    }

}
