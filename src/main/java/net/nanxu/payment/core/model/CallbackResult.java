package net.nanxu.payment.core.model;

import lombok.Data;

/**
 * CallbackResult.
 *
 * @author: P
 **/
@Data
public class CallbackResult {
    /**
     * 返回给支付商的内容
     */
    public Object render;
}
