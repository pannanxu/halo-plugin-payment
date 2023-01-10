package io.mvvm.halo.plugins.payment.sdk;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * SDK 提供的上下文.
 *
 * @author: pan
 **/
public final class SdkContextHolder implements ApplicationContextAware {
    /**
     * 插件主体的上下文。
     * <p>
     * TODO 目前存在问题：子插件无法注入父插件的bean，因此提供父插件的上下文用于获取bean。
     */
    @Getter
    private static ApplicationContext ctx;

    private static PaymentRegister register;
    @Getter
    private static PaymentDispatcher dispatcher;
    @Getter
    private static PayEnvironmentFetcher environmentFetcher;

    public static void register(IPaymentOperator operator) {
        register.register(operator);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        SdkContextHolder.ctx = applicationContext;
        SdkContextHolder.dispatcher = applicationContext.getBean(PaymentDispatcher.class);
        SdkContextHolder.register = applicationContext.getBean(PaymentRegister.class);
        SdkContextHolder.environmentFetcher = applicationContext.getBean(PayEnvironmentFetcher.class);
    }
}
