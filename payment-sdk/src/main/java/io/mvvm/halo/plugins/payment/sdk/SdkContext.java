package io.mvvm.halo.plugins.payment.sdk;

import org.springframework.context.ApplicationContext;

/**
 * SDK 提供的上下文.
 *
 * @author: pan
 **/
public final class SdkContext {
    /**
     * 插件主体的上下文。
     * <p>
     * TODO 目前存在问题：子插件无法注入父插件的bean，因此提供父插件的上下文用于获取bean。
     */
    public static ApplicationContext paymentCtx;

}
