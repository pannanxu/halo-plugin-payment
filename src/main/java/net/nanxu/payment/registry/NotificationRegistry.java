package net.nanxu.payment.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.nanxu.payment.infra.INotification;

/**
 * NotificationRegistry.
 *
 * @author: P
 **/
public class NotificationRegistry {

    private final Map<String, INotification> notification = new ConcurrentHashMap<>();

    public void register(INotification business) {
        notification.put(business.getName(), business);
    }

    public void unregister(INotification business) {
        notification.remove(business.getName());
    }

    public INotification getNotification(String name) {
        return notification.get(name);
    }
}
