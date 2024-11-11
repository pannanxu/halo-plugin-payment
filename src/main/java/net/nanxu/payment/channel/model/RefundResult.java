package net.nanxu.payment.channel.model;

import lombok.Builder;
import lombok.Data;

/**
 * RefundResult.
 *
 * @author: P
 **/
@Data
@Builder
public class RefundResult {
    /**
     * 订单单号
     */
    private String orderNo;
    /**
     * 外部订单号
     */
    private String outTradeNo;
    /**
     * 是否成功
     */
    private Boolean success;
}
