package io.mvvm.halo.plugins.payment.biz;

import io.mvvm.halo.plugins.payment.sdk.NotifyCallback;
import io.mvvm.halo.plugins.payment.sdk.response.AsyncNotifyResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * ExampleNotifyCallback.
 *
 * @author: pan
 **/
@Slf4j
@Component
public class ExampleNotifyCallback implements NotifyCallback {

    public static final String gvk =  "example_biz_notify";
    @Getter
    private final PluginWrapper pluginWrapper;

    public ExampleNotifyCallback(PluginWrapper pluginWrapper) {
        this.pluginWrapper = pluginWrapper;
    }

    @Override
    public String getGvk() {
        return gvk;
    }

    @Override
    public Mono<Boolean> payment(AsyncNotifyResponse response) {
        log.debug("支付成功");
        return Mono.just(true);
    }

    @Override
    public Mono<Boolean> refund(AsyncNotifyResponse response) {
        log.debug("退款成功");
        return Mono.just(true);
    }
}
