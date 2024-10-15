package net.nanxu.payment;

import lombok.Getter;
import net.nanxu.payment.infra.INotificationBusiness;
import net.nanxu.payment.infra.IPayment;
import net.nanxu.payment.infra.PaymentProfile;
import net.nanxu.payment.infra.model.PaymentSupport;
import net.nanxu.payment.registry.BusinessRegistry;
import net.nanxu.payment.registry.PaymentRegistry;
import net.nanxu.payment.router.PaymentRouter;
import net.nanxu.payment.security.SecurityRegistry;
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

    private final PaymentRegistry paymentRegistry;
    private final PaymentRouter router;
    private final SecurityRegistry security;
    private final BusinessRegistry businessRegistry;
    @Getter
    private final ServiceFactory serviceFactory;

    public PaymentFactory() {
        this.paymentRegistry = new PaymentRegistry();
        this.router = new PaymentRouter(paymentRegistry);
        this.security = new SecurityRegistry();
        this.businessRegistry = new BusinessRegistry();
        this.serviceFactory = ServiceFactory.create(paymentRegistry, businessRegistry, security);
    }

    /**
     * 根据请求获取可以在当前场景下使用的支付方式
     */
    public Flux<PaymentProfile> getPaymentProfiles(PaymentSupport order) {
        return router.selectPayments(order).map(IPayment::getProfile);
    }

    /**
     * 根据名称获取支付方式
     */
    public Mono<IPayment> getPayment(String name) {
        return Mono.justOrEmpty(paymentRegistry.get(name)).map(IPayment::wrap);
    }

    /**
     * 注册支付方式
     */
    public void register(IPayment payment) {
        paymentRegistry.register(payment);
    }

    public void unregister(IPayment payment) {
        paymentRegistry.unregister(payment);
    }

    public void register(INotificationBusiness notification) {
        businessRegistry.register(notification);
    }

    public void unregister(INotificationBusiness notification) {
        businessRegistry.unregister(notification);
    }

}
