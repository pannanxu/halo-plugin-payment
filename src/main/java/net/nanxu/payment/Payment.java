package net.nanxu.payment;

import lombok.NonNull;
import net.nanxu.payment.infra.INotificationBusiness;
import net.nanxu.payment.infra.IPayment;
import net.nanxu.payment.infra.PaymentProfile;
import net.nanxu.payment.infra.model.Order;
import net.nanxu.payment.infra.model.PaymentSupport;
import net.nanxu.payment.service.OrderService;
import net.nanxu.payment.service.CallbackService;
import net.nanxu.payment.service.PaymentService;
import net.nanxu.payment.service.ServiceFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 提供了一些静态方法.
 *
 * @author: P
 **/
@Component
public final class Payment implements ApplicationContextAware {

    private static PaymentFactory factory;

    public static Flux<PaymentProfile> getPaymentProfiles(PaymentSupport order) {
        return factory.getPaymentProfiles(order);
    }

    public static Mono<IPayment> getPayment(String name) {
        return factory.getPayment(name);
    }

    public static void register(IPayment payment) {
        factory.register(payment);
    }

    public static void unregister(IPayment payment) {
        factory.unregister(payment);
    }

    public static void register(INotificationBusiness notification) {
        factory.register(notification);
    }

    public static void unregister(INotificationBusiness notification) {
        factory.unregister(notification);
    }

    public static ServiceFactory getServiceFactory() {
        return factory.getServiceFactory();
    }

    public static Mono<Order> createOrder(Order order) {
        return getOrderService().createOrder(order);
    }

    public static PaymentService getPaymentService() {
        return getServiceFactory().getPayment();
    }

    public static OrderService getOrderService() {
        return getServiceFactory().getOrder();
    }

    public static CallbackService getCallbackService() {
        return getServiceFactory().getCallback();
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        Payment.factory = applicationContext.getBean(PaymentFactory.class);
    }
}
