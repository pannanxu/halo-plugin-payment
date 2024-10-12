package net.nanxu.payment;

import lombok.Builder;
import lombok.Data;
import net.nanxu.order.Order;
import org.springframework.web.reactive.function.server.ServerRequest;

/**
 * PaymentOrder.
 *
 * @author: P
 **/
@Data
@Builder
public class PaymentOrder {
    
    private Order order;
    
    private ServerRequest request;

    private String userAgent;
}
