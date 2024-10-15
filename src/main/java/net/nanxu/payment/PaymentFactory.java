package net.nanxu.payment;

import net.nanxu.payment.infra.INotificationBusiness;
import net.nanxu.payment.infra.IPayment;
import net.nanxu.payment.infra.PaymentProfile;
import net.nanxu.payment.infra.model.PaymentSupport;
import net.nanxu.payment.service.ServiceFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 提供给第三方使用的入口.
 *
 * @author: P
 **/
@Component
public class PaymentFactory {

    private final DispatcherPayment dispatcher;
    private final ServiceFactory factory;

    public PaymentFactory() {
        this.dispatcher = new DispatcherPayment();
        this.factory = ServiceFactory.create(dispatcher);
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

    public void unregister(IPayment payment) {
        dispatcher.unregister(payment);
    }

    public void register(INotificationBusiness notification) {
        dispatcher.register(notification);
    }

    public void unregister(INotificationBusiness notification) {
        dispatcher.unregister(notification);
    }

    public ServiceFactory getServiceFactory() {
        return factory;
    }

}
