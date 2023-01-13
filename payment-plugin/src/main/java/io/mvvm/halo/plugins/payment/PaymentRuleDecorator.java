package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.rule.BlackListRule;
import io.mvvm.halo.plugins.payment.rule.CacheRule;
import io.mvvm.halo.plugins.payment.rule.LimitRule;
import io.mvvm.halo.plugins.payment.rule.LimitRuleContext;
import io.mvvm.halo.plugins.payment.rule.ParameterVerificationRule;
import io.mvvm.halo.plugins.payment.rule.PaymentRule;
import io.mvvm.halo.plugins.payment.rule.PaymentRuleContext;
import io.mvvm.halo.plugins.payment.rule.PaymentStatusRule;
import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.PayEnvironmentFetcher;
import io.mvvm.halo.plugins.payment.sdk.PaymentDescriptor;
import io.mvvm.halo.plugins.payment.sdk.PaymentResponseWrapper;
import io.mvvm.halo.plugins.payment.sdk.cache.CacheManager;
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

/**
 * ExtensionPaymentDecorator.
 *
 * @author: pan
 **/
@Slf4j
public class PaymentRuleDecorator implements IPayment {

    private final IPayment payment;
    private final PaymentRule rootRule;

    public PaymentRuleDecorator(IPayment payment,
                                ReactiveExtensionClient client,
                                PayEnvironmentFetcher fetcher,
                                LimitRuleContext limitRuleContext,
                                CacheManager cacheManager) {
        this.payment = payment;
        this.rootRule = new PaymentRuleContext(client)
                .addHead(new BlackListRule(payment, fetcher))
                .addLast(new LimitRule(payment, limitRuleContext))
                .addLast(new PaymentStatusRule(payment))
                .addLast(new ParameterVerificationRule(payment))
                .addLast(new CacheRule(payment, cacheManager))
                .getFirst();
    }

    @Override
    public PaymentDescriptor getDescriptor() {
        return payment.getDescriptor();
    }

    @Override
    public Mono<Boolean> status() {
        return rootRule.status();
    }

    @Override
    public Mono<PaymentResponseWrapper<CreatePaymentResponse>> create(CreatePaymentRequest request) {
        try {
            return rootRule.create(request)
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
            return rootRule.fetch(request)
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
            return rootRule.cancel(request)
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
            return rootRule.refund(request)
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
            return rootRule.fetchRefund(request)
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
            return rootRule.paymentAsyncNotify(request)
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
            return rootRule.refundAsyncNotify(request)
                    .onErrorResume(BaseException.class, ex -> Mono.just(AsyncNotifyResponse.onError(ex)))
                    .onErrorResume(ex -> Mono.just(AsyncNotifyResponse.onError(ex)));
        } catch (Exception ex) {
            log.error("PaymentRuleDecorator|refundAsyncNotify|{}", ex.getMessage(), ex);
            return Mono.just(AsyncNotifyResponse.onError(ex));
        }
    }

}
