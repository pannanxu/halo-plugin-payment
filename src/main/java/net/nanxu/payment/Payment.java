package net.nanxu.payment;

import lombok.NonNull;
import net.nanxu.payment.account.IAccount;
import net.nanxu.payment.infra.IPayment;
import net.nanxu.payment.infra.PaymentProfile;
import net.nanxu.payment.infra.model.Order;
import net.nanxu.payment.infra.model.PaymentRequest;
import net.nanxu.payment.infra.model.PaymentResult;
import net.nanxu.payment.infra.model.PaymentSupport;
import net.nanxu.payment.infra.model.QueryRequest;
import net.nanxu.payment.infra.model.QueryResult;
import net.nanxu.payment.infra.model.RefundRequest;
import net.nanxu.payment.infra.model.RefundResult;
import net.nanxu.payment.service.CallbackService;
import net.nanxu.payment.service.OrderService;
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

    /**
     * 获取原始的支付通道
     */
    public static Mono<IPayment> getPayment(String name) {
        return factory.getPayment(name);
    }

    public static ServiceFactory getServiceFactory() {
        return factory.getServiceFactory();
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

    public static Mono<IAccount> getAccount(String name) {
        return getServiceFactory().getAccount().getAccount(name);
    }

    public static Mono<Order> createOrder(Order order) {
        return getOrderService().createOrder(order);
    }

    public static Mono<PaymentResult> pay(PaymentRequest request) {
        return getPaymentService().pay(request);
    }

    public static Mono<QueryResult> query(QueryRequest request) {
        return getPaymentService().query(request);
    }

    public static Mono<RefundResult> refund(RefundRequest request) {
        return getPaymentService().refund(request);
    }

    public static Mono<RefundResult> cancel(RefundRequest request) {
        return getPaymentService().cancel(request);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext)
        throws BeansException {
        Payment.factory = applicationContext.getBean(PaymentFactory.class);
    }
}
