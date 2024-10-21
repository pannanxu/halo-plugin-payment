package net.nanxu.payment.order;

import reactor.core.publisher.Mono;

/**
 * OrderService.
 *
 * @author: P
 **/
public interface OrderService {
    
    Mono<Order> getOrder(String orderNo);
    
    Mono<Order> createOrder(Order order);

    Mono<Order> updateOrder(Order order);
}
