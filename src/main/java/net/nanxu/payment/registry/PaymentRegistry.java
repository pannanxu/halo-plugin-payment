package net.nanxu.payment.registry;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import net.nanxu.payment.core.IPayment;

/**
 * PaymentRegistry.
 *
 * @author: P
 **/
@Slf4j
public class PaymentRegistry {

    private final Map<String, IPayment> payments = new ConcurrentHashMap<>();

    private final Lock lock = new ReentrantLock();

    public PaymentRegistry() {
    }

    public void register(IPayment payment) {
        try {
            if (lock.tryLock()) {
                IPayment pay = payments.get(payment.getName());
                unregister(pay);
                payment.register();
                payments.put(payment.getName(), payment);
            }
        } catch (Exception ex) {
            log.error("PaymentRegistry register error", ex);
        } finally {
            lock.unlock();
        }
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
