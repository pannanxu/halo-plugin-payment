package net.nanxu.payment.infra.model;

import lombok.Builder;
import lombok.Data;
import net.nanxu.payment.account.IAccount;
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
    private String channel;
    private String business;
    
    private IAccount account;

    private ServerRequest request;
}
