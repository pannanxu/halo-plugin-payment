package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.callback.NotifyCallbackManager;
import io.mvvm.halo.plugins.payment.sdk.PaymentRegister;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginWrapper;

/**
 * PaymentOperatorRegister.
 *
 * @author: pan
 **/
@Slf4j
public class PaymentOperatorRegister implements PaymentRegister {
    private final PluginStartedListener listener;

    public PaymentOperatorRegister(PaymentOperatorManager provider,
                                   NotifyCallbackManager notifyCallbackProvider) {
        this.listener = new PluginStartedListener(provider, notifyCallbackProvider);
    }

    @Override
    public void register(PluginWrapper wrapper) {
        log.debug("准备注册插件: {}", wrapper.getPluginId());
        listener.init(wrapper);
    }

}
