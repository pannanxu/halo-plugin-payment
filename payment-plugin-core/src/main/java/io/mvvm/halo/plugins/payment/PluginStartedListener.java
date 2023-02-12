package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.callback.NotifyCallbackManager;
import io.mvvm.halo.plugins.payment.sdk.IPaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.NotifyCallback;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;

import java.util.stream.Stream;

/**
 * PluginStartedListener.
 *
 * @author: pan
 **/
@Slf4j
public class PluginStartedListener {

    private final PaymentOperatorManager provider;
    private final NotifyCallbackManager notifyCallbackProvider;

    public PluginStartedListener(PaymentOperatorManager provider,
                                 NotifyCallbackManager notifyCallbackProvider) {
        this.provider = provider;
        this.notifyCallbackProvider = notifyCallbackProvider;
    }

    public void init(PluginWrapper wrapper) {
        // 添加插件启动停止的事件
        wrapper.getPluginManager().addPluginStateListener(event -> {
            if (PluginState.STARTED.equals(event.getPluginState())) {
                started(event.getPlugin());
            } else if (PluginState.STOPPED.equals(event.getPluginState())) {
                stopped(event.getPlugin());
                log.debug("支付插件 {} 卸载成功", event.getPlugin().getPluginId());
            }
        });
    }

    private void started(PluginWrapper wrapper) {
        getWrapperOperatorExtensions(wrapper).forEach(provider::register);
        getBizExtensions(wrapper).forEach(notifyCallbackProvider::register);
    }

    private void stopped(PluginWrapper wrapper) {
        provider.unregister(wrapper.getPluginId());
    }

    Stream<IPaymentOperator> getWrapperOperatorExtensions(PluginWrapper wrapper) {
        return wrapper.getPluginManager()
                .getExtensions(IPaymentOperator.class)
                .stream()
                .filter(e -> e.getPluginWrapper().getPluginId().equals(wrapper.getPluginId()));
    }

    Stream<NotifyCallback> getBizExtensions(PluginWrapper wrapper) {
        return wrapper.getPluginManager()
                .getExtensions(NotifyCallback.class)
                .stream()
                .filter(e -> e.getPluginWrapper().getPluginId().equals(wrapper.getPluginId()));
    }
}
