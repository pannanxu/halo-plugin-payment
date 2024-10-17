package net.nanxu.payment.acl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.nanxu.payment.reconciliation.IReconciliation;
import net.nanxu.payment.reconciliation.ReconciliationRegistry;
import org.springframework.stereotype.Component;
import run.halo.app.plugin.extensionpoint.ExtensionGetter;

/**
 * ReconciliationExtensionGetter.
 *
 * @author: P
 **/
@RequiredArgsConstructor
@Component
public class ReconciliationExtensionGetter implements ReconciliationRegistry {
    private final ExtensionGetter extensionGetter;

    public List<IReconciliation> getPayments() {
        return extensionGetter.getExtensionList(IReconciliation.class);
    }
}
