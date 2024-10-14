package net.nanxu.payment;

import net.nanxu.payment.infra.model.Order;
import net.nanxu.testplugin.AliPayment;
import net.nanxu.testplugin.WeChatPayment;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import run.halo.app.extension.SchemeManager;
import run.halo.app.plugin.BasePlugin;
import run.halo.app.plugin.PluginContext;

/**
 * <p>Plugin main class to manage the lifecycle of the plugin.</p>
 * <p>This class must be public and have a public constructor.</p>
 * <p>Only one main class extending {@link BasePlugin} is allowed per plugin.</p>
 *
 * @author guqing
 * @since 1.0.0
 */
@Component
public class StarterPlugin extends BasePlugin {

    private final DispatcherPayment dispatcher;
    private final SchemeManager schemeManager;

    public StarterPlugin(PluginContext pluginContext, SchemeManager schemeManager) {
        super(pluginContext);
        this.dispatcher = new DispatcherPayment();
        this.schemeManager = schemeManager;
    }

    @Bean
    public PaymentFactory payment() {
        return new PaymentFactory(dispatcher);
    }

    @Override
    public void start() {
        System.out.println("插件启动成功！");
        schemeManager.register(Order.class);
        
        dispatcher.register(new WeChatPayment());
        dispatcher.register(new AliPayment());
    }

    @Override
    public void stop() {
        System.out.println("插件停止！");
        schemeManager.unregister(schemeManager.get(Order.class));
        dispatcher.unregisterAll();
    }
}
