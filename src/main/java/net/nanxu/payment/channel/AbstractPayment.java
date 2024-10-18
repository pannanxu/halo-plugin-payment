package net.nanxu.payment.channel;

import java.util.List;
import lombok.Getter;
import net.nanxu.payment.channel.model.PaymentProfile;
import net.nanxu.payment.channel.model.SettingField;

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
    private final List<SettingField> settings;

    protected AbstractPayment(PaymentProfile profile, List<SettingField> settings, IPaymentSupport support,
        IPaymentCallback callback) {
        this.name = profile.getName();
        this.profile = profile;
        this.support = support;
        this.callback = callback;
        this.settings = settings;
    }

}
