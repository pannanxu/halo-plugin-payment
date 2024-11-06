package net.nanxu.payment.channel.model;

import lombok.Data;
import net.nanxu.payment.infra.ProtocolPacket;

/**
 * PayRequest.
 *
 * @author: P
 **/
@Data
public class PayRequest {
    /**
     * 单号
     */
    private String orderNo;
    /**
     * 支付通道
     */
    private String channel;
    /**
     * Http 数据包
     */
    private ProtocolPacket packet;
}
