package net.nanxu.payment.channel.model;

import lombok.Data;
import net.nanxu.payment.account.IAccount;
import net.nanxu.payment.order.Order;

/**
 * modelPaymentRequest.
 *
 * @author: P
 **/
@Data
public class PaymentRequest {
    private Order order;
    private IAccount account;
}
