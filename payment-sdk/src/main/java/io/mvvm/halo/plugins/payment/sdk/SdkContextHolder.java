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
//        SdkContextHolder.ctx = applicationContext;
        SdkContextHolder.dispatcher = applicationContext.getBean(PaymentDispatcher.class);
        SdkContextHolder.register = applicationContext.getBean(PaymentRegister.class);
        SdkContextHolder.environmentFetcher = applicationContext.getBean(PayEnvironmentFetcher.class);
    }
}
