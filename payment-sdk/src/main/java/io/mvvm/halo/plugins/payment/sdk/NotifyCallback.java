package io.mvvm.halo.plugins.payment.sdk;

import io.mvvm.halo.plugins.payment.sdk.response.AsyncNotifyResponse;
import org.pf4j.ExtensionPoint;
import org.pf4j.PluginWrapper;
import reactor.core.publisher.Mono;

/**
 * 业务层异步通知回调.
 * <p>
 * 注意：在0元商品、第三方不支持回调、同步退款等情况下不会执行。
 * <p>
 * 只有在成功在第三方支付下单并支付才会执行这里
 *
 * @author: pan
 **/
public interface NotifyCallback extends ExtensionPoint {

    PluginWrapper getPluginWrapper();

    /**
     * @return 唯一的标识，以英文、下划线组成。
     * <p>
     * 在下单时传递的 gvk 保持一致，当支付成功后收到的回调会根据此字段进行匹配并执行以下方法
     */
    String getGvk();

    /**
     * 当支付收到回调后，成功、取消等状态会执行此处理器，在业务中可以实现此处理器来完成业务状态的扭转
     */
    Mono<Boolean> payment(AsyncNotifyResponse response);

    /**
     * 当退款收到回调后，成功、取消等状态会执行此处理器，在业务中可以实现此处理器来完成业务状态的扭转
     */
    Mono<Boolean> refund(AsyncNotifyResponse response);

}
