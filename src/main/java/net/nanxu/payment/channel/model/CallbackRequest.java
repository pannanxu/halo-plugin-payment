package net.nanxu.payment.channel.model;

import lombok.Builder;
import lombok.Data;
import net.nanxu.payment.account.IAccount;
import net.nanxu.payment.order.Order;

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

    private String requestBody;
}
