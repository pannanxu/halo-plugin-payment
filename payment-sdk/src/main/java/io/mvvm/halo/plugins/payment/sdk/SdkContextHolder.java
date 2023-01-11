package io.mvvm.halo.plugins.payment.sdk;

import lombok.Getter;
import lombok.NonNull;
import org.pf4j.PluginWrapper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import run.halo.app.plugin.BasePlugin;

/**
 * SDK 提供的上下文.
 *
 * @author: pan
 **/
public final class SdkContextHolder implements ApplicationContextAware {

    private static ApplicationContext ctx;
    private static PaymentRegister register;

    public static Holder holder() {
        PaymentDispatcher dispatcher = SdkContextHolder.ctx.getBean(PaymentDispatcher.class);
        PayEnvironmentFetcher environmentFetcher = SdkContextHolder.ctx.getBean(PayEnvironmentFetcher.class);
        return new Holder(dispatcher, environmentFetcher);
    }

    public static void register(final PluginWrapper wrapper) {
        SdkContextHolder.register.register(wrapper);
    }

    public static void register(final BasePlugin plugin) {
        register(plugin.getWrapper());
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        SdkContextHolder.ctx = applicationContext;
        SdkContextHolder.register = SdkContextHolder.ctx.getBean(PaymentRegister.class);
    }

    public record Holder(@Getter PaymentDispatcher dispatcher,
                         @Getter PayEnvironmentFetcher environmentFetcher) {

    }
}
