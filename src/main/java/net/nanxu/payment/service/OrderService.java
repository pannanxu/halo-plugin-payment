package net.nanxu.payment.service;

import net.nanxu.payment.infra.model.Order;
import reactor.core.publisher.Mono;

/**
 * OrderService.
 *
 * @author: P
 **/
public interface OrderService {
    
    Order createSimpleOrder();
    
    Mono<Order> getOrder(String orderNo);
    
    Mono<Order> createOrder(Order order);

    Mono<Order> updateOrder(Order order);
}
