package io.mvvm.halo.plugins.payment.sdk;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * AbstractPaymentOperator.
 *
 * @author: pan
 **/
public abstract class AbstractPaymentOperator implements IPaymentOperator, ApplicationContextAware {

    protected final AtomicBoolean initStatusFlag = new AtomicBoolean(false);
    @Getter
    private ApplicationContext ctx;
    @Getter
    private PayEnvironmentFetcher environmentFetcher;

    @Getter
    private final WebClient client = WebClient.builder().build();

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        ctx = SdkContext.paymentCtx;
        environmentFetcher = ctx.getBean(PayEnvironmentFetcher.class);
        PaymentRegister register = ctx.getBean(PaymentRegister.class);
        register.register(this);
    }
}
