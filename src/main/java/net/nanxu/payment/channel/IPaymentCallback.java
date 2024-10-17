package net.nanxu.payment.channel;

import net.nanxu.payment.channel.model.CallbackRequest;
import net.nanxu.payment.channel.model.CallbackResult;
import reactor.core.publisher.Mono;

/**
 * 第三方支付插件回调.
 *
 * @author: P
 **/
public interface IPaymentCallback {
    /**
     * 支付回调
     */
    Mono<CallbackResult> callback(CallbackRequest request);

}
