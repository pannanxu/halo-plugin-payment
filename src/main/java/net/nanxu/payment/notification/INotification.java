package net.nanxu.payment.notification;

import net.nanxu.payment.order.Order;
import org.pf4j.ExtensionPoint;
import reactor.core.publisher.Mono;

/**
 * 通知第三方业务插件.
 *
 * @author: P
 **/
public interface INotification extends ExtensionPoint {

    String getName();

    /**
     * 第三方业务插件实现此方法，当支付成功后将会调用此方法。
     * <p>
     * 业务插件返回异常时将会重试三次。
     */
    Mono<Boolean> notify(Order order);

}
