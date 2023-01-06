package io.mvvm.halo.plugins.payment.sdk.async;

import io.mvvm.halo.plugins.payment.sdk.response.AsyncNotifyResponse;
import reactor.core.publisher.Mono;
import run.halo.app.extension.Ref;

/**
 * 支付异步通知业务层处理器.
 *
 * @author: pan
 **/
public interface AsyncNotifyHandler {

    Ref type();

    /**
     * 当支付收到回调后，成功、取消等状态会执行此处理器，在业务中可以实现此处理器来完成业务状态的扭转
     */
    Mono<Boolean> payment(AsyncNotifyResponse response);

    /**
     * 当退款收到回调后，成功、取消等状态会执行此处理器，在业务中可以实现此处理器来完成业务状态的扭转
     */
    Mono<Boolean> refund(AsyncNotifyResponse response);

}
