package net.nanxu.payment.reconciliation;

import reactor.core.publisher.Mono;

/**
 * ReconciliationService.
 *
 * @author: P
 **/
public interface ReconciliationService {
    
    Mono<Void> reconciliation();
    
}
