package net.nanxu.payment;

import lombok.Getter;
import net.nanxu.payment.infra.INotification;
import net.nanxu.payment.infra.IPayment;
import net.nanxu.payment.infra.PaymentProfile;
import net.nanxu.payment.infra.model.PaymentSupport;
import net.nanxu.payment.registry.NotificationRegistry;
import net.nanxu.payment.registry.PaymentRegistry;
import net.nanxu.payment.router.PaymentRouter;
import net.nanxu.payment.security.SecurityRegistry;
import net.nanxu.payment.service.ServiceFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ReactiveExtensionClient;

/**
 * 提供给第三方使用的入口.
 *
 * @author: P
 **/
@Component
public final class PaymentFactory {

    private final ReactiveExtensionClient client;

    private final PaymentRegistry paymentRegistry;
    private final PaymentRouter router;
    private final SecurityRegistry security;
    private final NotificationRegistry notificationRegistry;
    @Getter
    private final ServiceFactory serviceFactory;

    public PaymentFactory(ReactiveExtensionClient client) {
        this.client = client;
        this.paymentRegistry = new PaymentRegistry();
        this.router = new PaymentRouter(paymentRegistry);
        this.security = new SecurityRegistry();
        this.notificationRegistry = new NotificationRegistry();
        this.serviceFactory =
            ServiceFactory.create(paymentRegistry, notificationRegistry, security, client);
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
        return Mono.justOrEmpty(paymentRegistry.get(name));
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

    public void register(INotification notification) {
        notificationRegistry.register(notification);
    }

    public void unregister(INotification notification) {
        notificationRegistry.unregister(notification);
    }

}
