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


    public HaloPluginPayment(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        System.out.println("插件启动成功！");
    }

    @Override
    public void stop() {
        System.out.println("插件停止！");
    }
}
