package net.nanxu.payment.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import net.nanxu.payment.infra.model.Money;
import net.nanxu.payment.infra.model.Order;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import run.halo.app.extension.Ref;

/**
 * OrderService.
 *
 * @author: P
 **/
@Service
public class OrderServiceImpl implements OrderService {

    @Override
    public Order createSimpleOrder() {
        return Order.createOrder()
            .setOrderNo(UUID.randomUUID().toString().replaceAll("-", ""))
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
    public Mono<Order> createOrder(Order order) {
        return Mono.just(createSimpleOrder());
    }

    @Override
    public Mono<Order> updateOrder(Order order) {
        return Mono.just(order);
    }

}
