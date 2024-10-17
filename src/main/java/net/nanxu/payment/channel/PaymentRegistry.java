package net.nanxu.payment.channel;

import java.util.List;

/**
 * PaymentRegistry.
 *
 * @author: P
 **/
public interface PaymentRegistry {
    
    IPayment get(String name);
    
    List<IPayment> getPayments();

    // private final PaymentExtensionGetter paymentExtensionGetter;
    //
    // public PaymentRegistry(PaymentExtensionGetter paymentExtensionGetter) {
    //     this.paymentExtensionGetter = paymentExtensionGetter;
    // }
    //
    // public IPayment get(String name) {
    //     return getPayments().stream().filter(e -> e.getName().equals(name)).findFirst().orElseThrow(() -> new PaymentException("不支持此支付通道"));
    // }
    //
    // public List<IPayment> getPayments() {
    //     return paymentExtensionGetter.getPaymentExtensions();
    // }
}
