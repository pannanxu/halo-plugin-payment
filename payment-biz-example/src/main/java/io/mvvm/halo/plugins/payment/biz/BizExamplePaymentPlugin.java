package io.mvvm.halo.plugins.payment.biz;

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
public class BizExamplePaymentPlugin extends BasePlugin {

    public static final String name = "BizExamplePayment";

    public BizExamplePaymentPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        System.out.println("聚合支付业务集成测试插件启动成功！");
        SdkContextHolder.register(this);
    }

    @Override
    public void stop() {
        System.out.println("聚合支付业务集成测试插件停止！");
    }
}
