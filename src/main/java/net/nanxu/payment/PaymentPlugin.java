package net.nanxu.payment;

import net.nanxu.payment.infra.IPayment;
import net.nanxu.payment.infra.model.Order;
import net.nanxu.testplugin.AliPayment;
import net.nanxu.testplugin.WeChatPayment;
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
public class PaymentPlugin extends BasePlugin {

    private final SchemeManager schemeManager;
    
    private final IPayment wechat = new WeChatPayment();
    private final IPayment ali = new AliPayment();

    public PaymentPlugin(PluginContext pluginContext, SchemeManager schemeManager) {
        super(pluginContext);
        this.schemeManager = schemeManager;
    }

    @Override
    public void start() {
        schemeManager.register(Order.class);

        Payment.register(wechat);
        Payment.register(ali);

        System.out.println("插件启动成功！");
    }

    @Override
    public void stop() {
        schemeManager.unregister(schemeManager.get(Order.class));

        Payment.unregister(wechat);
        Payment.unregister(ali);

        System.out.println("插件停止！");
    }
}
