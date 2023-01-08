package io.mvvm.halo.plugins.payment.sdk;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.function.client.WebClient;
import run.halo.app.extension.Unstructured;
import run.halo.app.infra.utils.YamlUnstructuredLoader;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * AbstractPaymentOperator.
 *
 * @author: pan
 **/
@Slf4j
public abstract class AbstractPaymentOperator implements IPaymentOperator, ApplicationContextAware {

    protected final AtomicBoolean initStatusFlag = new AtomicBoolean(false);
    protected final Unstructured userInputFormSchema = loadUserInputFormSchema();
    @Getter
    private final WebClient client = WebClient.builder().build();
    @Getter
    private ApplicationContext ctx;
    @Getter
    private PayEnvironmentFetcher environmentFetcher;

    /**
     * 加载支付时需要用户输入的数据.
     */
    protected Unstructured loadUserInputFormSchema() {
        Unstructured userInput = null;
        try {
            List<Unstructured> list = new YamlUnstructuredLoader(
                    new ClassPathResource("extensions/payment-user-input.yaml", this.getClass().getClassLoader()))
                    .load();
            if (!list.isEmpty()) {
                userInput = list.get(0);
            }
        } catch (Exception ex) {
            log.error("加载用户输入配置失败: {}", ex.getMessage(), ex);
        }
        return userInput;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        ctx = SdkContext.paymentCtx;
        environmentFetcher = ctx.getBean(PayEnvironmentFetcher.class);
        PaymentRegister register = ctx.getBean(PaymentRegister.class);
        register.register(this);
    }
}
