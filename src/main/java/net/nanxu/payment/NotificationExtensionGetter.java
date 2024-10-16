package net.nanxu.payment;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.nanxu.payment.infra.INotification;
import org.springframework.stereotype.Component;
import run.halo.app.plugin.extensionpoint.ExtensionGetter;

/**
 * NotificationExtensionGetter.
 *
 * @author: P
 **/
@RequiredArgsConstructor
@Component
public class NotificationExtensionGetter {
    private final ExtensionGetter extensionGetter;

    public List<INotification> getNotificationExtensions() {
        return extensionGetter.getExtensionList(INotification.class);
    }

}
