package net.nanxu.payment.channel.model;

import lombok.Builder;
import lombok.Data;
import net.nanxu.payment.infra.ProtocolPacket;
import net.nanxu.payment.order.Order;

/**
 * PaymentOrder.
 *
 * @author: P
 **/
@Data
@Builder
public class PaymentSupport {
    
    private Order order;
    
    private ProtocolPacket packet;
}
