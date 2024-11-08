package net.nanxu.payment.order.event;

import lombok.Getter;
import net.nanxu.payment.order.Order;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

/**
 * 订单已取消
 *
 * @author: P
 **/
@Getter
public class OrderCanceledEvent extends ApplicationEvent {
    
    private final Order order;
    
    public OrderCanceledEvent(Object source, Order order) {
        super(source);
        this.order = order;
    }

    public OrderCanceledEvent(Object source, Clock clock, Order order) {
        super(source, clock);
        this.order = order;
    }
}
