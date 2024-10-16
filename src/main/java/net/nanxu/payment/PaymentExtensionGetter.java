package net.nanxu.payment;

import lombok.RequiredArgsConstructor;
import net.nanxu.payment.infra.IPayment;
import org.springframework.stereotype.Component;
import run.halo.app.plugin.extensionpoint.ExtensionGetter;
import java.util.List;

/**
 * PaymentExtensionGetter.
 *
 * @author: P
 **/
@RequiredArgsConstructor
@Component
public class PaymentExtensionGetter {
    private final ExtensionGetter extensionGetter;

    public List<IPayment> getPaymentExtensions() {
        return extensionGetter.getExtensionList(IPayment.class);
    }

}
