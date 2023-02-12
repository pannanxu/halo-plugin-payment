package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.IPaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.PayEnvironmentFetcher;
import io.mvvm.halo.plugins.payment.sdk.PaymentDescriptorGetter;
import io.mvvm.halo.plugins.payment.sdk.PaymentExtension;
import io.mvvm.halo.plugins.payment.sdk.PaymentResponseWrapper;
import io.mvvm.halo.plugins.payment.sdk.PaymentSetting;
import io.mvvm.halo.plugins.payment.sdk.cache.CacheManager;
import io.mvvm.halo.plugins.payment.sdk.enums.PaymentMode;
import io.mvvm.halo.plugins.payment.sdk.enums.PaymentStatus;
import io.mvvm.halo.plugins.payment.sdk.exception.BaseException;
import io.mvvm.halo.plugins.payment.sdk.request.CreatePaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.FetchRefundPaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.PaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.RefundPaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.response.AsyncNotifyResponse;
import io.mvvm.halo.plugins.payment.sdk.response.CreatePaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentInfo;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.RefundPaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.infra.ExternalUrlSupplier;

/**
 * SimplePayment.
 *
 * @author: pan
 **/
@Slf4j
public class SimplePayment implements IPayment {

    private final PaymentDescriptorGetter descriptorGetter;
    private final ExternalUrlSupplier externalUrlSupplier;
    private final IPaymentOperator operator;
    private final PayEnvironmentFetcher fetcher;
    private final CacheManager cacheManager;
    private final ReactiveExtensionClient client;

    public SimplePayment(IPaymentOperator operator,
                         ExternalUrlSupplier externalUrlSupplier,
                         PayEnvironmentFetcher fetcher,
                         CacheManager cacheManager, ReactiveExtensionClient client) {
        this.operator = operator;
        this.externalUrlSupplier = externalUrlSupplier;
        this.fetcher = fetcher;
        descriptorGetter = PaymentDescriptorGetter.of(operator, operator::status);
        this.cacheManager = cacheManager;
        this.client = client;
    }

    @Override
    public PaymentDescriptorGetter getDescriptor() {
        return descriptorGetter;
    }

    @Override
    public Mono<Boolean> status() {
        return Mono.just(operator.status())
                .filter(e -> e)
                .switchIfEmpty(Mono.defer(() -> client.fetch(PaymentExtension.class, getDescriptor().getName())
                        .map(e -> e.getSpec().getEnabled())))
                .switchIfEmpty(Mono.just(Boolean.FALSE));
    }

    @Override
    public Mono<PaymentResponseWrapper<CreatePaymentResponse>> create(CreatePaymentRequest request) {
        // 0 元订单直接返回支付成功
        if (request.getMoney().isFree()) {
            PaymentResponseWrapper<CreatePaymentResponse> wrapper = new PaymentResponseWrapper<>();
            wrapper.setDescriptor(descriptorGetter);
            wrapper.setResponse(new CreatePaymentResponse()
                    .setSuccess(true)
                    .setPaymentMode(PaymentMode.none.name())
                    .setOutTradeNo(request.getOutTradeNo())
                    .setStatus(PaymentStatus.payment_successful)
                    .setMoney(request.getMoney())
                    .setExpand(request.getExpand()));
            return Mono.just(wrapper);
        }
        try {
            return fetcher.fetchPaymentConfig(PaymentSetting.basic)
                    .flatMap(setting -> {
                        String token = setting.getToken();
                        request.setNotifyUrl(externalUrlSupplier.get().toString(), token, getDescriptor().getName());
                        return operator.create(request).map(response -> new PaymentResponseWrapper<>(response, getDescriptor()));
                    })
                    .onErrorResume(BaseException.class, ex -> Mono.just(new PaymentResponseWrapper<>(CreatePaymentResponse.onError(ex), getDescriptor())))
                    .onErrorResume(ex -> Mono.just(new PaymentResponseWrapper<>(CreatePaymentResponse.onError(ex), getDescriptor())));
        } catch (Exception ex) {
            log.error("PaymentRuleDecorator|create|{}", ex.getMessage(), ex);
            return Mono.just(new PaymentResponseWrapper<>(CreatePaymentResponse.onError(ex), getDescriptor()));
        }
    }

    @Override
    public Mono<PaymentResponseWrapper<PaymentInfo>> fetch(PaymentRequest request) {
        try {
            return operator.fetch(request).map(response -> new PaymentResponseWrapper<>(response, getDescriptor()))
                    .onErrorResume(BaseException.class, ex -> Mono.just(new PaymentResponseWrapper<>(PaymentInfo.onError(ex), getDescriptor())))
                    .onErrorResume(ex -> Mono.just(new PaymentResponseWrapper<>(PaymentInfo.onError(ex), getDescriptor())));
        } catch (Exception ex) {
            log.error("PaymentRuleDecorator|fetch|{}", ex.getMessage(), ex);
            return Mono.just(new PaymentResponseWrapper<>(PaymentInfo.onError(ex), getDescriptor()));
        }
    }

    @Override
    public Mono<PaymentResponseWrapper<PaymentResponse>> cancel(PaymentRequest request) {
        try {
            return operator.cancel(request).map(response -> new PaymentResponseWrapper<>(response, getDescriptor()))
                    .onErrorResume(BaseException.class, ex -> Mono.just(new PaymentResponseWrapper<>(PaymentInfo.onError(ex), getDescriptor())))
                    .onErrorResume(ex -> Mono.just(new PaymentResponseWrapper<>(PaymentInfo.onError(ex), getDescriptor())));
        } catch (Exception ex) {
            log.error("PaymentRuleDecorator|cancel|{}", ex.getMessage(), ex);
            return Mono.just(new PaymentResponseWrapper<>(PaymentInfo.onError(ex), getDescriptor()));
        }
    }

    @Override
    public Mono<PaymentResponseWrapper<RefundPaymentResponse>> refund(RefundPaymentRequest request) {
        try {
            return fetcher.fetchPaymentConfig(PaymentSetting.basic)
                    .flatMap(setting -> {
                        request.setRefundNotifyUrl(externalUrlSupplier.get().toString(), setting.getToken(), getDescriptor().getName());
                        return operator.refund(request).map(response -> new PaymentResponseWrapper<>(response, getDescriptor()));
                    })
                    .onErrorResume(BaseException.class, ex -> Mono.just(new PaymentResponseWrapper<>(RefundPaymentResponse.onError(ex), getDescriptor())))
                    .onErrorResume(ex -> Mono.just(new PaymentResponseWrapper<>(RefundPaymentResponse.onError(ex), getDescriptor())));
        } catch (Exception ex) {
            log.error("PaymentRuleDecorator|refund|{}", ex.getMessage(), ex);
            return Mono.just(new PaymentResponseWrapper<>(RefundPaymentResponse.onError(ex), getDescriptor()));
        }
    }

    @Override
    public Mono<PaymentResponseWrapper<RefundPaymentResponse>> fetchRefund(FetchRefundPaymentRequest request) {
        try {
            return operator.fetchRefund(request).map(response -> new PaymentResponseWrapper<>(response, getDescriptor()))
                    .onErrorResume(BaseException.class, ex -> Mono.just(new PaymentResponseWrapper<>(RefundPaymentResponse.onError(ex), getDescriptor())))
                    .onErrorResume(ex -> Mono.just(new PaymentResponseWrapper<>(RefundPaymentResponse.onError(ex), getDescriptor())));
        } catch (Exception ex) {
            log.error("PaymentRuleDecorator|fetchRefund|{}", ex.getMessage(), ex);
            return Mono.just(new PaymentResponseWrapper<>(RefundPaymentResponse.onError(ex), getDescriptor()));
        }
    }

    @Override
    public Mono<AsyncNotifyResponse> paymentAsyncNotify(ServerRequest request) {
        try {
            return fetcher.fetchPaymentConfig(PaymentSetting.basic)
                    .flatMap(setting -> {
                        String token = request.pathVariable("token");
                        if (setting.getToken().equals(token)) {
                            return operator.paymentAsyncNotify(request);
                        }
                        return Mono.error(new BaseException("token 不匹配"));
                    })
                    .onErrorResume(BaseException.class, ex -> Mono.just(AsyncNotifyResponse.onError(ex)))
                    .onErrorResume(ex -> Mono.just(AsyncNotifyResponse.onError(ex)));
        } catch (Exception ex) {
            log.error("PaymentRuleDecorator|paymentAsyncNotify|{}", ex.getMessage(), ex);
            return Mono.just(AsyncNotifyResponse.onError(ex));
        }
    }

    @Override
    public Mono<AsyncNotifyResponse> refundAsyncNotify(ServerRequest request) {
        try {
            return fetcher.fetchPaymentConfig(PaymentSetting.basic)
                    .flatMap(setting -> {
                        String token = request.pathVariable("token");
                        if (setting.getToken().equals(token)) {
                            return operator.refundAsyncNotify(request);
                        }
                        return Mono.error(new BaseException("token 不匹配"));
                    })
                    .onErrorResume(BaseException.class, ex -> Mono.just(AsyncNotifyResponse.onError(ex)))
                    .onErrorResume(ex -> Mono.just(AsyncNotifyResponse.onError(ex)));
        } catch (Exception ex) {
            log.error("PaymentRuleDecorator|refundAsyncNotify|{}", ex.getMessage(), ex);
            return Mono.just(AsyncNotifyResponse.onError(ex));
        }
    }

    @Override
    public String toString() {
        return """
                SimplePayment: %s, name: %s
                """.formatted(operator, operator.getDescriptor().getName());
    }
}
