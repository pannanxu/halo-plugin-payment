package net.nanxu.payment.router;

import net.nanxu.payment.infra.model.PaymentSupport;
import net.nanxu.payment.infra.IPayment;
import net.nanxu.payment.registry.PaymentRegistry;
import reactor.core.publisher.Flux;

/**
 * PaymentRouter.
 *
 * @author: P
 **/
public class PaymentRouter {
    private final PaymentRegistry registry;

    public PaymentRouter(PaymentRegistry registry) {
        this.registry = registry;
    }

    public Flux<IPayment> selectPayments(PaymentSupport order) {
        return Flux.fromIterable(registry.getPayments())
                .filterWhen(e -> e.getSupport().pay(order));
    }
}
