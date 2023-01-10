package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.PaymentExtension;
import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import run.halo.app.extension.SchemeManager;
import run.halo.app.plugin.BasePlugin;

/**
 * PaymentPlugin.
 *
 * @author: pan
 **/
@Component
public class HaloPluginPayment extends BasePlugin {

    public static final String name = "Payment";

    private final SchemeManager schemeManager;

    public HaloPluginPayment(PluginWrapper wrapper, SchemeManager schemeManager) {
        super(wrapper);
        this.schemeManager = schemeManager;
    }

    @Override
    public void start() {
        System.out.println("插件启动成功！");
        schemeManager.register(PaymentExtension.class);
    }

    @Override
    public void stop() {
        System.out.println("插件停止！");
        schemeManager.unregister(schemeManager.get(PaymentExtension.class));
    }
}
