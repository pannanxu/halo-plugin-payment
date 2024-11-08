package net.nanxu.payment.endpoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nanxu.payment.order.Order;
import net.nanxu.payment.order.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.ApiVersion;

/**
 * OrderController.
 *
 * @author: P
 **/
@ApiVersion("order.nanxu.net/v1alpha1")
@RequestMapping("/orders")
@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{orderNo}/status/pay")
    public Mono<Order.PayStatus> payStatus(@PathVariable String orderNo) {
        return orderService.getOrder(orderNo).map(Order::getPayStatus);
    }

}
