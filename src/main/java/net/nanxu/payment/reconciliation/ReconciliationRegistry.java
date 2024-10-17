package net.nanxu.payment.reconciliation;

import java.util.List;

/**
 * ReconciliationRegistry.
 *
 * @author: P
 **/
public interface ReconciliationRegistry {
    List<IReconciliation> getPayments();
}
