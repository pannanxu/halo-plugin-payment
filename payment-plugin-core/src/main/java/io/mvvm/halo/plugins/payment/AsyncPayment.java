package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.PaymentDispatcher;
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
    private final NotifyCallbackProvider provider;

    public AsyncPayment(PaymentDispatcher dispatcher, NotifyCallbackProvider provider) {
        this.dispatcher = dispatcher;
        this.provider = provider;
    }

    @Override
    public Mono<Object> paymentAsyncNotify(ServerRequest request, String gvk, String paymentType) {
        return dispatcher.dispatch(paymentType)
                .flatMap(payment -> payment.paymentAsyncNotify(request))
                .flatMap(response -> {
                    if (!response.isSuccess()) {
                        log.debug("支付异步通知执行失败: {}", response);
                        return Mono.justOrEmpty(response.getResponseFail().get());
                    }
                    return provider.getPoint(gvk)
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
                .flatMap(payment -> payment.refundAsyncNotify(request))
                .flatMap(response -> {
                    if (!response.isSuccess()) {
                        log.debug("退款异步通知执行失败: {}", response);
                        return Mono.justOrEmpty(response.getResponseFail().get());
                    }
                    return provider.getPoint(gvk)
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
