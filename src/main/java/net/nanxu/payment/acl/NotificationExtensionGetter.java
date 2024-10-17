package net.nanxu.payment.acl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.nanxu.payment.exception.NotificationException;
import net.nanxu.payment.notification.INotification;
import net.nanxu.payment.notification.NotificationRegistry;
import org.springframework.stereotype.Component;
import run.halo.app.plugin.extensionpoint.ExtensionGetter;

/**
 * NotificationExtensionGetter.
 *
 * @author: P
 **/
@RequiredArgsConstructor
@Component
public class NotificationExtensionGetter implements NotificationRegistry {
    private final ExtensionGetter extensionGetter;

    public List<INotification> getNotificationExtensions() {
        return extensionGetter.getExtensionList(INotification.class);
    }

    @Override
    public INotification getNotification(String name) {
        return getNotificationExtensions().stream()
                .filter(e -> e.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new NotificationException("不支持此通知类型"));
    }
}
