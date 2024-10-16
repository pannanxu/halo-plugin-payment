package net.nanxu.testplugin;

import net.nanxu.payment.infra.INotification;
import net.nanxu.payment.infra.model.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * TestNotificationBusiness.
 *
 * @author: P
 **/
@Component
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