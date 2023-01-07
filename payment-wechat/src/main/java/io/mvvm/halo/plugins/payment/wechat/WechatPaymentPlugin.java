package io.mvvm.halo.plugins.payment.wechat;

import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import run.halo.app.plugin.BasePlugin;

/**
 * WechatPaymentPlugin.
 *
 * @author: pan
 **/
@Component
public class WechatPaymentPlugin extends BasePlugin {

    public static final String name = "WechatPayment";


    public WechatPaymentPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        System.out.println("微信支付插件启动成功！");
    }

    @Override
    public void stop() {
        System.out.println("微信支付插件停止！");
    }
}
