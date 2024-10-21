package net.nanxu.payment.channel.model;

import lombok.Builder;
import lombok.Data;

/**
 * CallbackResult.
 *
 * @author: P
 **/
@Data
@Builder
public class CallbackResult {
    /**
     * 返回给支付商的内容
     */
    public Object render;
    /**
     * 是否支付成功
     */
    private Boolean success;
}
