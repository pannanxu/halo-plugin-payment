package net.nanxu.payment;

import lombok.Getter;
import net.nanxu.payment.core.IPayment;
import net.nanxu.payment.core.IPaymentCallback;
import net.nanxu.payment.core.IPaymentSupport;
import net.nanxu.payment.core.PaymentProfile;
import net.nanxu.payment.core.model.PaymentRequest;
import net.nanxu.payment.core.model.PaymentResult;
import net.nanxu.payment.core.model.QueryRequest;
import net.nanxu.payment.core.model.QueryResult;
import net.nanxu.payment.core.model.RefundRequest;
import net.nanxu.payment.core.model.RefundResult;
import net.nanxu.payment.impl.AliPayment;
import net.nanxu.payment.impl.WeChatPayment;
import net.nanxu.payment.registry.PaymentRegistry;
import net.nanxu.payment.router.PaymentRouter;
import net.nanxu.payment.security.SecurityModule;
import net.nanxu.payment.security.PaymentBeforeSecurityModule;
import net.nanxu.payment.security.SecurityModuleContext;
import net.nanxu.payment.security.SecurityRegistry;
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

    /**
     * 根据请求获取可以在当前场景下使用的支付方式
     */
    public Flux<PaymentProfile> getPaymentProfiles(PaymentOrder order) {
        return getRouter().selectPayments(order).map(IPayment::getProfile);
    }

    /**
     * 根据名称获取支付方式
     */
    public Mono<IPayment> getPayment(String name) {
        return Mono.justOrEmpty(getRegistry().get(name)).map(e -> new PaymentProxy(e, security));
    }

    public void register(IPayment payment) {
        registry.register(payment);
    }

    /**
     * 取消注册所有的支付方式
     */
    public void unregisterAll() {
        registry.unregisterAll();
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
