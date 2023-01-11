package io.mvvm.halo.plugins.payment.alipay;

import io.mvvm.halo.plugins.payment.sdk.SdkContextHolder;
import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import run.halo.app.plugin.BasePlugin;

/**
 * 支付宝.
 *
 * @author: pan
 **/
@Component
public class AliPaymentPlugin extends BasePlugin {

    public static final String name = "AliPayment";

    public AliPaymentPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        System.out.println("支付宝插件启动成功！");
        SdkContextHolder.register(this);
    }

    @Override
    public void stop() {
        System.out.println("支付宝插件停止！");
    }
}
