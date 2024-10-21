package net.nanxu.plugin.business;

import net.nanxu.payment.business.IBusiness;
import net.nanxu.payment.order.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * TestNotificationBusiness.
 *
 * @author: P
 **/
@Component
public class TestBusiness implements IBusiness {
    @Override
    public String getName() {
        return "test-notification-plugin";
    }

    @Override
    public Mono<Boolean> notify(Order order) {
        return Mono.just(true);
    }
}
