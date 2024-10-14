package net.nanxu.payment.infra;

import lombok.Getter;

/**
 * AbstractPayment.
 *
 * @author: P
 **/
@Getter
public abstract class AbstractPayment implements IPayment {

    private final String name;
    private final PaymentProfile profile;
    private final IPaymentSupport support;
    private final IPaymentCallback callback;

    protected AbstractPayment(String name, PaymentProfile profile, IPaymentSupport support, IPaymentCallback callback) {
        this.name = name;
        this.profile = profile;
        this.support = support;
        this.callback = callback;
    }

}
