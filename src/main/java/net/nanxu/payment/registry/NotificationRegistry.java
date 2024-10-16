package net.nanxu.payment.registry;

import net.nanxu.payment.NotificationExtensionGetter;
import net.nanxu.payment.exception.NotificationException;
import net.nanxu.payment.infra.INotification;

/**
 * NotificationRegistry.
 *
 * @author: P
 **/
public class NotificationRegistry {

    private final NotificationExtensionGetter notificationExtensionGetter;

    public NotificationRegistry(NotificationExtensionGetter notificationExtensionGetter) {
        this.notificationExtensionGetter = notificationExtensionGetter;
    }

    public INotification getNotification(String name) {
        return notificationExtensionGetter.getNotificationExtensions().stream()
            .filter(e -> e.getName().equals(name))
            .findFirst()
            .orElseThrow(() -> new NotificationException("不支持此通知类型"));
    }
}
