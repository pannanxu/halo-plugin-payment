package io.mvvm.halo.plugins.payment.sdk;

import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * AbstractPaymentOperator.
 *
 * @author: pan
 **/
public abstract class AbstractPaymentOperator implements IPaymentOperator, ApplicationContextAware {

    protected ApplicationContext ctx;

    protected PayEnvironmentFetcher environmentFetcher;

    protected final AtomicBoolean initStatusFlag = new AtomicBoolean(false);

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
        environmentFetcher = ctx.getBean(PayEnvironmentFetcher.class);
        PaymentRegister register = ctx.getBean(PaymentRegister.class);
        register.register(this);
    }
}
