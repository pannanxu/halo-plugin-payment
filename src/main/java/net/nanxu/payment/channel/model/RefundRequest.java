package net.nanxu.payment.channel.model;

import lombok.Builder;
import lombok.Data;
import net.nanxu.payment.account.IAccount;
import net.nanxu.payment.money.Money;

/**
 * RefundRequest.
 *
 * @author: P
 **/
@Data
@Builder
public class RefundRequest {
    /**
     * 退款单号
     */
    private String refundNo;
    /**
     * 退款金额
     */
    private Money money;
    /**
     * 总金额
     */
    private Money total;
    /**
     * 订单单号
     */
    private String orderNo;
    /**
     * 外部订单号
     */
    private String outTradeNo;
    /**
     * 退款原因
     */
    private String remark;
    /**
     * 退款通知地址
     */
    private String notifyUrl;
    /**
     * 支付账户
     */
    private IAccount account;
}
