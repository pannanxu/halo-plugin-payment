package net.nanxu.payment;

import lombok.NonNull;
import net.nanxu.payment.account.IAccount;
import net.nanxu.payment.channel.CallbackService;
import net.nanxu.payment.channel.IPayment;
import net.nanxu.payment.channel.IPaymentRouter;
import net.nanxu.payment.channel.PaymentService;
import net.nanxu.payment.channel.model.PayRequest;
import net.nanxu.payment.channel.model.PaymentResult;
import net.nanxu.payment.channel.model.QueryRequest;
import net.nanxu.payment.channel.model.QueryResult;
import net.nanxu.payment.channel.model.RefundRequest;
import net.nanxu.payment.channel.model.RefundResult;
import net.nanxu.payment.order.Order;
import net.nanxu.payment.order.OrderService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 提供了一些静态方法.
 *
 * @author: P
 **/
@Component
public final class Payment implements ApplicationContextAware {

    private static PaymentFactory factory;

    /**
     * 获取原始的支付通道
     */
    public static Mono<IPayment> getPayment(String name) {
        return factory.getPayment(name);
    }

    public static PaymentService getPaymentService() {
        return factory.getServiceFactory().getPayment();
    }

    public static OrderService getOrderService() {
        return factory.getServiceFactory().getOrder();
    }

    public static CallbackService getCallbackService() {
        return factory.getServiceFactory().getCallback();
    }

    public static IPaymentRouter getPaymentRouter() {
        return factory.getPaymentRouter();
    }

    public static Mono<IAccount> getAccount(String name) {
        return factory.getServiceFactory().getAccount().getAccount(name);
    }

    public static Mono<Order> createOrder(Order order) {
        return getOrderService().createOrder(order);
    }

    public static Mono<PaymentResult> pay(PayRequest request) {
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
