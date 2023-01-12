package io.mvvm.halo.plugins.payment.sdk;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * PaymentDispatcher.
 *
 * @author: pan
 **/
public interface PaymentDispatcher {

    String payment = "payment";

    /**
     * 使用名称获取到指定的支付方式
     */
    Mono<IPayment> dispatch(String payment);

    /**
     * 从 reactor 上下文中获取指定支付方式
     */
    Mono<IPayment> dispatch();

    /**
     * @return 获取所有以初始化的支付模式
     */
    Flux<IPayment> payments();

    Flux<IPayment> payments(PaymentQuery query);
}
