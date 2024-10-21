package net.nanxu.payment.channel.model;

import lombok.Data;
import net.nanxu.payment.account.IAccount;
import net.nanxu.payment.order.Order;

/**
 * QueryRequest.
 *
 * @author: P
 **/
@Data
public class QueryRequest {
    private Order order;
    private IAccount account;
}
