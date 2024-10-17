package net.nanxu.payment.acl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.nanxu.payment.channel.IPayment;
import net.nanxu.payment.exception.PaymentException;
import net.nanxu.payment.channel.PaymentRegistry;
import org.springframework.stereotype.Component;
import run.halo.app.plugin.extensionpoint.ExtensionGetter;

/**
 * PaymentExtensionGetter.
 *
 * @author: P
 **/
@RequiredArgsConstructor
@Component
public class PaymentExtensionGetter implements PaymentRegistry {

    private final ExtensionGetter extensionGetter;

    @Override
    public IPayment get(String name) {
        return getPayments().stream().filter(e -> e.getName().equals(name)).findFirst()
            .orElseThrow(() -> new PaymentException("不支持此支付通道"));
    }

    @Override
    public List<IPayment> getPayments() {
        return extensionGetter.getExtensionList(IPayment.class);
    }
}
