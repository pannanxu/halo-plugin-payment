package net.nanxu.payment.channel.model;

import lombok.Builder;
import lombok.Data;
import net.nanxu.payment.order.Order;
import org.springframework.web.reactive.function.server.ServerRequest;

/**
 * PaymentOrder.
 *
 * @author: P
 **/
@Data
@Builder
public class PaymentSupport {
    
    private Order order;
    
    private ServerRequest request;

    private String userAgent;
}
