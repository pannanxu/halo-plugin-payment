package net.nanxu.payment.service;

import lombok.Getter;
import net.nanxu.payment.registry.BusinessRegistry;
import net.nanxu.payment.registry.PaymentRegistry;
import net.nanxu.payment.security.SecurityRegistry;
import net.nanxu.payment.service.impl.CallbackServiceImpl;
import net.nanxu.payment.service.impl.OrderServiceImpl;
import net.nanxu.payment.service.impl.PaymentServiceImpl;

/**
 * ServiceFactory.
 *
 * @author: P
 **/
@Getter
public class ServiceFactory {
    private final PaymentService payment;
    private final OrderService order;
    private final CallbackService callback;

    public ServiceFactory(PaymentService payment, OrderService order,
        CallbackService callback) {
        this.payment = payment;
        this.order = order;
        this.callback = callback;
    }

    public static ServiceFactory create(PaymentRegistry registry,
        BusinessRegistry businessRegistry,
        SecurityRegistry security) {
        OrderService orderService = new OrderServiceImpl();
        PaymentService paymentService =
            new PaymentServiceImpl(security, businessRegistry, orderService);
        CallbackService callbackService =
            new CallbackServiceImpl(businessRegistry, registry, orderService);
        return new ServiceFactory(paymentService, orderService, callbackService);
    }

}
