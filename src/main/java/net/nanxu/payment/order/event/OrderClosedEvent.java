package net.nanxu.payment.order.event;

import lombok.Getter;
import net.nanxu.payment.order.Order;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

/**
 * 订单已关闭：点击收货、关闭订单、取消订单、订单超时、全部退款
 *
 * @author: P
 **/
@Getter
public class OrderClosedEvent extends ApplicationEvent {
    
    private final Order order;
    
    public OrderClosedEvent(Object source, Order order) {
        super(source);
        this.order = order;
    }

    public OrderClosedEvent(Object source, Clock clock, Order order) {
        super(source, clock);
        this.order = order;
    }
}
