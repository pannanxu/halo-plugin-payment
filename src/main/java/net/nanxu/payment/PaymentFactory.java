package net.nanxu.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.nanxu.payment.account.AccountService;
import net.nanxu.payment.channel.CallbackService;
import net.nanxu.payment.channel.IPayment;
import net.nanxu.payment.channel.PaymentRegistry;
import net.nanxu.payment.channel.PaymentService;
import net.nanxu.payment.channel.model.PaymentProfile;
import net.nanxu.payment.channel.model.PaymentSupport;
import net.nanxu.payment.order.OrderService;
import net.nanxu.payment.router.PaymentRouter;
import net.nanxu.payment.security.SecurityRegistry;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 提供给第三方使用的入口.
 *
 * @author: P
 **/
@Component
public final class PaymentFactory {

    private final PaymentRegistry paymentRegistry;
    private final PaymentRouter router;
    private final SecurityRegistry securityRegistry;
    @Getter
    private final ServiceFactory serviceFactory;

    public PaymentFactory(PaymentRegistry paymentRegistry, ServiceFactory serviceFactory) {
        this.paymentRegistry = paymentRegistry;
        this.router = new PaymentRouter(paymentRegistry);
        this.serviceFactory = serviceFactory;
        this.securityRegistry = new SecurityRegistry();
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

    @Getter
    @Component
    @RequiredArgsConstructor
    public static final class ServiceFactory {
        private final PaymentService payment;
        private final OrderService order;
        private final CallbackService callback;
        private final AccountService account;
    }

}
