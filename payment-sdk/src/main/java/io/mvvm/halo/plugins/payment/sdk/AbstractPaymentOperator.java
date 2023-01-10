package io.mvvm.halo.plugins.payment.sdk;

import io.mvvm.halo.plugins.payment.sdk.utils.GsonUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
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
    protected final Unstructured userInputFormSchema;
    protected GsonUtils gson;
    @Getter
    @Setter
    private WebClient client;
    @Getter
    private PayEnvironmentFetcher environmentFetcher;

    public AbstractPaymentOperator() {
        this.userInputFormSchema = null;
    }

    public AbstractPaymentOperator(boolean isLoadFormSchema) {
        if (isLoadFormSchema) {
            this.userInputFormSchema = loadUserInputFormSchema();
        } else {
            this.userInputFormSchema = null;
        }
    }

    /**
     * 加载支付时需要用户输入的数据.
     */
    protected Unstructured loadUserInputFormSchema() {
        Unstructured userInput = null;
        String name = getDescriptor().getName();
        try {
            List<Unstructured> list = new YamlUnstructuredLoader(
                    new ClassPathResource("user-extensions/" + name + ".yaml", this.getClass().getClassLoader()))
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
    public boolean status() {
        return initStatusFlag.get();
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        environmentFetcher = SdkContextHolder.getEnvironmentFetcher();
        SdkContextHolder.register(this);
    }
}
