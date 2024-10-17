package net.nanxu.payment.notification;

/**
 * NotificationRegistry.
 *
 * @author: P
 **/
public interface NotificationRegistry {

    INotification getNotification(String name);

}
