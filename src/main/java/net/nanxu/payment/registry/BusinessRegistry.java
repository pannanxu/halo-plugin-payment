package net.nanxu.payment.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.nanxu.payment.infra.INotificationBusiness;

/**
 * BusinessRegistry.
 *
 * @author: P
 **/
public class BusinessRegistry {

    private final Map<String, INotificationBusiness> notification = new ConcurrentHashMap<>();

    public void register(INotificationBusiness business) {
        notification.put(business.getName(), business);
    }

    public void unregister(String name) {
        notification.remove(name);
    }

    public INotificationBusiness getNotification(String name) {
        return notification.get(name);
    }
}
