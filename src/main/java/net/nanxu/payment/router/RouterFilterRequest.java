package net.nanxu.payment.router;

import lombok.Builder;
import lombok.Data;
import net.nanxu.payment.infra.ProtocolPacket;
import net.nanxu.payment.order.Order;

/**
 * RouterFilterRequest.
 *
 * @author: P
 **/
@Data
@Builder
public class RouterFilterRequest {

    /**
     * 订单信息
     */
    private Order order;

    private ProtocolPacket packet;
}
