package net.nanxu.payment.registry;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.nanxu.payment.PaymentExtensionGetter;
import net.nanxu.payment.exception.PaymentException;
import net.nanxu.payment.infra.IPayment;

/**
 * PaymentRegistry.
 *
 * @author: P
 **/
@Slf4j
public class PaymentRegistry {

    private final PaymentExtensionGetter paymentExtensionGetter;

    public PaymentRegistry(PaymentExtensionGetter paymentExtensionGetter) {
        this.paymentExtensionGetter = paymentExtensionGetter;
    }

    public IPayment get(String name) {
        return getPayments().stream().filter(e -> e.getName().equals(name)).findFirst().orElseThrow(() -> new PaymentException("不支持此支付通道"));
    }

    public List<IPayment> getPayments() {
        return paymentExtensionGetter.getPaymentExtensions();
    }
}
