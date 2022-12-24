package io.mvvm.halo.plugins.payment.sdk.async;

import io.mvvm.halo.plugins.payment.sdk.IPayment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

/**
 * AsyncPayment.
 *
 * @author: pan
 **/
@Slf4j
public abstract class AbstractAsyncPayment {

    private final AsyncNotifyManager asyncNotifyManager;

    public AbstractAsyncPayment(AsyncNotifyManager asyncNotifyManager) {
        this.asyncNotifyManager = asyncNotifyManager;
    }

    protected abstract Mono<IPayment> dispatch(ServerRequest request);

    public Mono<Object> async(ServerRequest request) {
        return dispatch(request)
                .flatMap(payment -> payment.getOperator().asyncNotify(request))
                .flatMap(response -> {
                    if (!response.isSuccess()) {
                        log.debug("异步通知执行失败: {}", response);
                        return Mono.error(new RuntimeException("异步通知失败"));
                    }
                    return asyncNotifyManager.get(response.getGvk())
                            .handle(response)
                            .thenReturn(response.getResponse());
                });
    }
}
