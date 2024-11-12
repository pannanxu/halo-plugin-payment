package net.nanxu.payment.channel.model;

import lombok.Builder;
import lombok.Data;
import net.nanxu.payment.money.Money;

/**
 * RefundResult.
 *
 * @author: P
 **/
@Data
@Builder
public class RefundResult {
    /**
     * 退款单号
     */
    private String refundNo;
    /**
     * 退款金额
     */
    private Money money;
    /**
     * 订单单号
     */
    private String orderNo;
    /**
     * 外部订单号
     */
    private String outTradeNo;
    /**
     * 是否成功, 此成功不代表最终的金额交易，紧急代表接口调用是否成功
     */
    private Boolean success;
}
