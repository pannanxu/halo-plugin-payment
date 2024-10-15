package net.nanxu.payment.service;

import lombok.Getter;
import net.nanxu.payment.DispatcherPayment;
import net.nanxu.payment.service.impl.OrderServiceImpl;
import net.nanxu.payment.service.impl.CallbackServiceImpl;
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

    public static ServiceFactory create(DispatcherPayment dispatcher) {
        OrderService orderService = new OrderServiceImpl();
        PaymentService paymentService = new PaymentServiceImpl(dispatcher.getSecurity(),
            dispatcher.getBusinessRegistry(), orderService);
        CallbackService callbackService =
            new CallbackServiceImpl(dispatcher.getBusinessRegistry(),
                dispatcher.getRegistry(), orderService);
        return new ServiceFactory(paymentService, orderService, callbackService);
    }

}
