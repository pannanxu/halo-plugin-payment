package net.nanxu.payment.infra;

import net.nanxu.payment.infra.model.CallbackRequest;
import net.nanxu.payment.infra.model.CallbackResult;
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
