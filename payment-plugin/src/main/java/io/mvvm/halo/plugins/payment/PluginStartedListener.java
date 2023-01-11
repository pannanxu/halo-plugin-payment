package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.IPaymentOperator;
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

    private final PaymentProvider provider;
    private final PluginWrapper wrapper;

    public PluginStartedListener(PaymentProvider provider, PluginWrapper wrapper) {
        this.provider = provider;
        this.wrapper = wrapper;
    }

    public void init() {
        // 添加插件启动停止的事件
        this.wrapper.getPluginManager().addPluginStateListener(event -> {
            if (PluginState.STARTED.equals(event.getPluginState())) {
                started();
            } else if (PluginState.STOPPED.equals(event.getPluginState())) {
                stopped();
            }
        });
    }

    private void started() {
        getWrapperOperatorExtensions().forEach(provider::register);
    }

    private void stopped() {
        getWrapperOperatorExtensions().forEach(provider::unregister);
    }

    Stream<IPaymentOperator> getWrapperOperatorExtensions() {
        return this.wrapper.getPluginManager()
                .getExtensions(IPaymentOperator.class)
                .stream()
                .filter(e -> e.getPluginWrapper().equals(wrapper));
    }
}
