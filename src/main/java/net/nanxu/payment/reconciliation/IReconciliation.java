package net.nanxu.payment.reconciliation;

import org.pf4j.ExtensionPoint;
import reactor.core.publisher.Flux;

/**
 * IReconciliation.
 *
 * @author: P
 **/
public interface IReconciliation extends ExtensionPoint {
    /**
     * 第三方支付通过此接口返回商户的订单信息
     *
     * @param request
     * @return
     */
    Flux<ReconciliationOrder> reconciliation(ReconciliationRequest request);

}
