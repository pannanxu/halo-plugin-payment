package net.nanxu.testplugin;

import net.nanxu.payment.infra.INotificationBusiness;
import net.nanxu.payment.infra.model.Order;
import reactor.core.publisher.Mono;

/**
 * TestNotificationBusiness.
 *
 * @author: P
 **/
public class TestNotificationBusiness implements INotificationBusiness {
    @Override
    public String getName() {
        return "test-notification-plugin";
    }

    @Override
    public Mono<Boolean> notify(Order order) {
        return Mono.just(true);
    }
}
