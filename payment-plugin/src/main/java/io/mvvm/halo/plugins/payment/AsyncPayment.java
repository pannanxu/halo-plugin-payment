package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.PaymentDispatcher;
import io.mvvm.halo.plugins.payment.sdk.async.AsyncNotifyManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

/**
 * AsyncPayment.
 *
 * @author: pan
 **/
@Slf4j
public class AsyncPayment implements IAsyncPayment {
    private final PaymentDispatcher dispatcher;
    private final AsyncNotifyManager asyncNotifyManager;

    public AsyncPayment(PaymentDispatcher dispatcher,
                        AsyncNotifyManager asyncNotifyManager) {
        this.dispatcher = dispatcher;
        this.asyncNotifyManager = asyncNotifyManager;
    }

    @Override
    public Mono<Object> async(ServerRequest request, String gvk, String paymentType) {
        return dispatcher.dispatch(paymentType)
                .flatMap(payment -> payment.getOperator().asyncNotify(request))
                .flatMap(response -> {
                    if (!response.isSuccess()) {
                        log.debug("异步通知执行失败: {}", response);
                        return Mono.error(new RuntimeException("异步通知失败"));
                    }
                    return asyncNotifyManager.get(gvk)
                            .handle(response)
                            .thenReturn(response.getResponse());
                });
    }
}
