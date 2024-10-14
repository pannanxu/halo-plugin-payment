package net.nanxu.payment;

import net.nanxu.payment.infra.IPayment;
import net.nanxu.payment.infra.PaymentProfile;
import net.nanxu.payment.infra.model.PaymentSupport;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 提供给第三方使用的入口.
 *
 * @author: P
 **/
public class PaymentFactory {

    private final DispatcherPayment dispatcher;

    public PaymentFactory(DispatcherPayment dispatcher) {
        this.dispatcher = dispatcher;
    }

    /**
     * 根据请求获取可以在当前场景下使用的支付方式
     */
    public Flux<PaymentProfile> getPaymentProfiles(PaymentSupport order) {
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
