package io.mvvm.halo.plugins.payment;

import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import run.halo.app.plugin.BasePlugin;

/**
 * PaymentPlugin.
 *
 * @author: pan
 **/
@Component
public class HaloPluginPayment extends BasePlugin {

    public static final String name = "Payment";

    private final PaymentPluginStarted paymentPluginStarted;

    public HaloPluginPayment(PluginWrapper wrapper,
                             PaymentPluginStarted paymentPluginStarted) {
        super(wrapper);
        this.paymentPluginStarted = paymentPluginStarted;
    }

    @Override
    public void start() {
        System.out.println("插件启动成功！");
        paymentPluginStarted.start();
    }

    @Override
    public void stop() {
        System.out.println("插件停止！");
        paymentPluginStarted.stop();
    }
}
