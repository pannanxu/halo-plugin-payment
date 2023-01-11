package io.mvvm.halo.plugins.payment.sdk;

import org.pf4j.PluginWrapper;

/**
 * PaymentRegister.
 *
 * @author: pan
 **/
public interface PaymentRegister {
    /**
     * 注册插件。
     * <p>
     * {@link SdkContextHolder#register(PluginWrapper)}
     */
    void register(PluginWrapper wrapper);

}
