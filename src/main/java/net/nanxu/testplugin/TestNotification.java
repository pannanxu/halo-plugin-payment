package net.nanxu.testplugin;

import net.nanxu.payment.infra.INotification;
import net.nanxu.payment.infra.model.Order;
import reactor.core.publisher.Mono;

/**
 * TestNotificationBusiness.
 *
 * @author: P
 **/
public class TestNotification implements INotification {
    @Override
    public String getName() {
        return "test-notification-plugin";
    }

    @Override
    public Mono<Boolean> notify(Order order) {
        return Mono.just(true);
    }
}
