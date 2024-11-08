package net.nanxu.payment.order.event;

import java.time.Clock;
import lombok.Getter;
import net.nanxu.payment.order.Order;
import org.springframework.context.ApplicationEvent;

/**
 * 订单已创建
 *
 * @author: P
 **/
@Getter
public class OrderCreatedEvent extends ApplicationEvent {
    
    private final Order order;
    
    public OrderCreatedEvent(Object source, Order order) {
        super(source);
        this.order = order;
    }

    public OrderCreatedEvent(Object source, Clock clock, Order order) {
        super(source, clock);
        this.order = order;
    }
}
