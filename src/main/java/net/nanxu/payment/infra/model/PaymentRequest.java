package net.nanxu.payment.infra.model;

import lombok.Data;
import net.nanxu.payment.account.IAccount;

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
