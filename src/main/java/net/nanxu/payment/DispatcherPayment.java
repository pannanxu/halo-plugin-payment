package net.nanxu.payment;

import lombok.Getter;
import net.nanxu.payment.infra.INotificationBusiness;
import net.nanxu.payment.infra.IPayment;
import net.nanxu.payment.infra.IPaymentCallback;
import net.nanxu.payment.infra.IPaymentSupport;
import net.nanxu.payment.infra.PaymentProfile;
import net.nanxu.payment.infra.model.PaymentRequest;
import net.nanxu.payment.infra.model.PaymentResult;
import net.nanxu.payment.infra.model.PaymentSupport;
import net.nanxu.payment.infra.model.QueryRequest;
import net.nanxu.payment.infra.model.QueryResult;
import net.nanxu.payment.infra.model.RefundRequest;
import net.nanxu.payment.infra.model.RefundResult;
import net.nanxu.payment.registry.BusinessRegistry;
import net.nanxu.payment.registry.PaymentRegistry;
import net.nanxu.payment.router.PaymentRouter;
import net.nanxu.payment.security.PaymentBeforeSecurityModule;
import net.nanxu.payment.security.SecurityModule;
import net.nanxu.payment.security.SecurityModuleContext;
import net.nanxu.payment.security.SecurityRegistry;
import net.nanxu.testplugin.AliPayment;
import net.nanxu.testplugin.WeChatPayment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * DispatcherPayment.
 *
 * @author: P
 **/
@Getter
public class DispatcherPayment {
    private final PaymentRegistry registry = new PaymentRegistry();
    private final PaymentRouter router = new PaymentRouter(registry);
    private final SecurityRegistry security = new SecurityRegistry();
    private final BusinessRegistry businessRegistry = new BusinessRegistry();

    /**
     * 根据请求获取可以在当前场景下使用的支付方式
     */
    public Flux<PaymentProfile> getPaymentProfiles(PaymentSupport order) {
        return getRouter().selectPayments(order).map(IPayment::getProfile);
    }

    /**
     * 根据名称获取支付方式
     */
    public Mono<IPayment> getPayment(String name) {
        return Mono.justOrEmpty(getRegistry().get(name));
    }

    public void register(IPayment payment) {
        registry.register(payment);
    }

    public void unregister(IPayment payment) {
        registry.unregister(payment);
    }

    public void register(INotificationBusiness notification) {
        businessRegistry.register(notification);
    }

    public void unregister(INotificationBusiness notification) {
        businessRegistry.unregister(notification);
    }

    public static class PaymentProxy implements IPayment {

        private final IPayment payment;
        private final SecurityRegistry security;

        public PaymentProxy(IPayment payment, SecurityRegistry security) {
            this.payment = payment;
            this.security = security;
        }

        @Override
        public String getName() {
            return payment.getName();
        }

        @Override
        public PaymentProfile getProfile() {
            return payment.getProfile();
        }

        @Override
        public IPaymentSupport getSupport() {
            return payment.getSupport();
        }

        @Override
        public IPaymentCallback getCallback() {
            return payment.getCallback();
        }

        @Override
        public Mono<PaymentResult> pay(PaymentRequest request) {
            return security.getModules(PaymentBeforeSecurityModule.class)
                .flatMap(module -> module.check(new SecurityModuleContext(request)))
                .next()
                .flatMap(type -> type == SecurityModule.Type.Reject 
                    ? Mono.error(new RuntimeException("Reject")) 
                    : payment.pay(request));
        }

        @Override
        public Mono<QueryResult> query(QueryRequest request) {
            return payment.query(request);
        }

        @Override
        public Mono<RefundResult> refund(RefundRequest request) {
            return payment.refund(request);
        }

        @Override
        public Mono<RefundResult> cancel(RefundRequest request) {
            return payment.cancel(request);
        }

        @Override
        public void register() {
            payment.register();
        }

        @Override
        public void unregister() {
            payment.unregister();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DispatcherPayment dispatcher = new DispatcherPayment();
        dispatcher.register(new WeChatPayment());
        dispatcher.register(new AliPayment());

        dispatcher.getPayment(WeChatPayment.NAME)
            .flatMap(payment -> payment.pay(new PaymentRequest()))
            .subscribe();
        
        Thread.sleep(100000);
    }
}
