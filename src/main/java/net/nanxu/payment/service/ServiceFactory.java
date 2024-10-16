package net.nanxu.payment.service;

import lombok.Getter;
import net.nanxu.payment.registry.NotificationRegistry;
import net.nanxu.payment.registry.PaymentRegistry;
import net.nanxu.payment.security.SecurityRegistry;
import net.nanxu.payment.service.impl.AccountServiceImpl;
import net.nanxu.payment.service.impl.CallbackServiceImpl;
import net.nanxu.payment.service.impl.OrderServiceImpl;
import net.nanxu.payment.service.impl.PaymentServiceImpl;
import run.halo.app.extension.ReactiveExtensionClient;

/**
 * ServiceFactory.
 *
 * @author: P
 **/
@Getter
public final class ServiceFactory {
    private final PaymentService payment;
    private final OrderService order;
    private final CallbackService callback;
    private final AccountService account;
    private final ReactiveExtensionClient client;

    public ServiceFactory(PaymentService payment, OrderService order,
        CallbackService callback, AccountService account, ReactiveExtensionClient client) {
        this.payment = payment;
        this.order = order;
        this.callback = callback;
        this.account = account;
        this.client = client;
    }

    public static ServiceFactory create(PaymentRegistry registry,
        NotificationRegistry notificationRegistry,
        SecurityRegistry security,
        ReactiveExtensionClient client) {
        AccountService accountService = new AccountServiceImpl(registry, client);
        OrderService orderService = new OrderServiceImpl();
        PaymentService paymentService = new PaymentServiceImpl(security, registry, orderService, accountService);
        CallbackService callbackService =
            new CallbackServiceImpl(notificationRegistry, registry, orderService, accountService);
        return new ServiceFactory(paymentService, orderService, callbackService, accountService, client);
    }

}
