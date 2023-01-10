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

    /**
     * @param device 设备标识
     * @return 获取具备指定设备的支付方式。如果同一种支付方式没有此设备时会返回默认的方式
     */
    Flux<IPayment> payments(String device);
}
