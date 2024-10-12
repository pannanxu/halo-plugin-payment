package net.nanxu.payment;

import net.nanxu.payment.core.IPayment;
import net.nanxu.payment.core.PaymentProfile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 提供给第三方使用的入口.
 *
 * @author: P
 **/
public class Payment {

    private final DispatcherPayment dispatcher;

    public Payment(DispatcherPayment dispatcher) {
        this.dispatcher = dispatcher;
    }

    /**
     * 根据请求获取可以在当前场景下使用的支付方式
     */
    public Flux<PaymentProfile> getPaymentProfiles(PaymentOrder order) {
        return dispatcher.getPaymentProfiles(order);
    }

    /**
     * 根据名称获取支付方式
     */
    public Mono<IPayment> getPayment(String name) {
        return dispatcher.getPayment(name).map(IPayment::wrap);
    }

    /**
     * 注册支付方式
     */
    public void register(IPayment payment) {
        dispatcher.register(payment);
    }

}
