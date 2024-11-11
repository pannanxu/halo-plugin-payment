package net.nanxu.payment.channel.model;

import lombok.Builder;
import lombok.Data;
import net.nanxu.payment.money.Money;
import net.nanxu.payment.order.Order;

/**
 * QueryResult.
 *
 * @author: P
 **/
@Data
@Builder
public class QueryResult {
    /**
     * 订单单号
     */
    private String orderNo;
    /**
     * 外部订单号
     */
    private String outTradeNo;
    
    private String method;
    /**
     * 订单状态
     */
    private Order.OrderStatus payStatus;
    /**
     * 金额
     */
    private Money money;

}
