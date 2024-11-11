package net.nanxu.payment.channel.model;

import lombok.Data;
import net.nanxu.payment.account.IAccount;

/**
 * RefundRequest.
 *
 * @author: P
 **/
@Data
public class RefundRequest {
    /**
     * 订单单号
     */
    private String orderNo;
    /**
     * 外部订单号
     */
    private String outTradeNo;
    /**
     * 支付账户
     */
    private IAccount account;
}
