package net.nanxu.payment.core.model;

import lombok.Builder;
import lombok.Data;
import net.nanxu.order.Order;
import org.springframework.web.reactive.function.server.ServerRequest;

/**
 * CallbackRequest.
 *
 * @author: P
 **/
@Data
@Builder
public class CallbackRequest {
    
    private Order order;
    
    private ServerRequest request;
}
