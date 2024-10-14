package net.nanxu.payment.infra.model;

import lombok.Builder;
import lombok.Data;
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

    private String orderNo;
    private String payment;
    private String business;
    
    private ServerRequest request;
}
