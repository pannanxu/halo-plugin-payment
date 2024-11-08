package net.nanxu.payment.order;

import reactor.core.publisher.Mono;

/**
 * OrderService.
 *
 * @author: P
 **/
public interface OrderService {
    /**
     * 查询订单
     */
    Mono<Order> getOrder(String orderNo);

    /**
     * 创建订单
     */
    Mono<Order> createOrder(Order order);

    @Deprecated
    default Mono<Order> updateOrder(Order order) {
        return Mono.empty();
    }

    /**
     * 修改订单已支付状态
     */
    Mono<Order> paidOrder(String orderNo);

    /**
     * 取消订单
     */
    Mono<Order> cancelOrder(String orderNo);

    /**
     * 关闭订单
     */
    Mono<Order> closeOrder(String orderNo);
}
