package net.nanxu.payment.infra.model;

import lombok.Builder;
import lombok.Data;
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
