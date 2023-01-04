package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.IPayment;
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
    public Mono<Object> paymentAsyncNotify(ServerRequest request, String gvk, String paymentType) {
        return dispatcher.dispatch(paymentType)
                .map(IPayment::getOperator)
                .flatMap(operator -> operator.paymentAsyncNotify(request))
                .flatMap(response -> {
                    if (!response.isSuccess()) {
                        log.debug("支付异步通知执行失败: {}", response);
                        return Mono.justOrEmpty(response.getResponseFail().get());
                    }
                    return asyncNotifyManager.get(gvk)
                            .payment(response)
                            .map(res -> {
                                if (res) {
                                    return response.getResponseSuccess().get();
                                }
                                return response.getResponseFail().get();
                            });
                });
    }

    @Override
    public Mono<Object> refundAsyncNotify(ServerRequest request, String gvk, String paymentType) {
        return dispatcher.dispatch(paymentType)
                .map(IPayment::getOperator)
                .flatMap(operator -> operator.refundAsyncNotify(request))
                .flatMap(response -> {
                    if (!response.isSuccess()) {
                        log.debug("退款异步通知执行失败: {}", response);
                        return Mono.justOrEmpty(response.getResponseFail().get());
                    }
                    return asyncNotifyManager.get(gvk)
                            .refund(response)
                            .map(res -> {
                                if (res) {
                                    return response.getResponseSuccess().get();
                                }
                                return response.getResponseFail().get();
                            });
                });
    }
}
