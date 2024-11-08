package net.nanxu.payment.order.event;

import lombok.Getter;
import net.nanxu.payment.order.Order;
import org.springframework.context.ApplicationEvent;
import java.time.Clock;

/**
 * 订单已支付.
 *
 * @author: P
 **/
@Getter
public class OrderPaidEvent extends ApplicationEvent {
    
    private final Order order;
    
    public OrderPaidEvent(Object source, Order order) {
        super(source);
        this.order = order;
    }

    public OrderPaidEvent(Object source, Clock clock, Order order) {
        super(source, clock);
        this.order = order;
    }
}
