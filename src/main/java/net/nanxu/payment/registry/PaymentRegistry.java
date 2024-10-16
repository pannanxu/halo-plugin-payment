package net.nanxu.payment.registry;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import net.nanxu.payment.infra.IPayment;

/**
 * PaymentRegistry.
 *
 * @author: P
 **/
@Slf4j
public class PaymentRegistry {

    private final Map<String, IPayment> payments = new ConcurrentHashMap<>();

    public PaymentRegistry() {
    }

    public void register(IPayment payment) {
        IPayment pay = payments.get(payment.getName());
        unregister(pay);
        payment.register();
        payments.put(payment.getName(), payment);
    }

    public void unregister(IPayment payment) {
        if (null != payment && payments.containsKey(payment.getName())) {
            payments.remove(payment.getName()).unregister();
        }
    }

    public void unregisterAll() {
        getPayments().forEach(this::unregister);
    }

    public void unregister(String name) {
        payments.remove(name);
    }

    public IPayment get(String name) {
        return payments.get(name);
    }

    public List<IPayment> getPayments() {
        return payments.values().stream().toList();
    }
}
