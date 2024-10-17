package net.nanxu.payment.reconciliation;

import lombok.RequiredArgsConstructor;
import net.nanxu.payment.order.OrderService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * ReconciliationServiceImpl.
 *
 * @author: P
 **/
@RequiredArgsConstructor
@Component
public class ReconciliationServiceImpl implements ReconciliationService{

    private final OrderService orderService;
    private final ReconciliationRegistry registry;

    @Override
    public Mono<Void> reconciliation() {
        return Mono.empty();
    }
}
