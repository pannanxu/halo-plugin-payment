package net.nanxu.payment.router;

import net.nanxu.payment.PaymentOrder;
import net.nanxu.payment.core.IPayment;
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

    public Flux<IPayment> selectPayments(PaymentOrder order) {
        return Flux.fromIterable(registry.getPayments())
                .filterWhen(e -> e.getSupport().pay(order));
    }
}
