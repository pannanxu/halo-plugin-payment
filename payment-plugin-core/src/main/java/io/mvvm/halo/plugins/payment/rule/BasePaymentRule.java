package io.mvvm.halo.plugins.payment.rule;

import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.PaymentDescriptor;
import io.mvvm.halo.plugins.payment.sdk.PaymentResponseWrapper;
import io.mvvm.halo.plugins.payment.sdk.request.CreatePaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.FetchRefundPaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.PaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.RefundPaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.response.AsyncNotifyResponse;
import io.mvvm.halo.plugins.payment.sdk.response.CreatePaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentInfo;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.RefundPaymentResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ReactiveExtensionClient;

/**
 * BasePaymentRule.
 *
 * @author: pan
 **/
public abstract class BasePaymentRule implements PaymentRule {

    protected final IPayment payment;
    @Setter
    protected ReactiveExtensionClient client;

    @Getter
    @Setter
    private BasePaymentRule next;

    public BasePaymentRule(IPayment payment) {
        this.payment = payment;
    }

    @Override
    public PaymentDescriptor getDescriptor() {
        if (null == next) {
            return payment.getDescriptor();
        }
        return next.getDescriptor();
    }

    @Override
    public Mono<Boolean> status() {
        if (null == next) {
            return payment.status();
        }
        return next.status();
    }

    @Override
    public Mono<PaymentResponseWrapper<CreatePaymentResponse>> create(CreatePaymentRequest request) {
        if (null == next) {
            return payment.create(request);
        }
        return next.create(request);
    }

    @Override
    public Mono<PaymentResponseWrapper<PaymentInfo>> fetch(PaymentRequest request) {
        if (null == next) {
            return payment.fetch(request);
        }
        return next.fetch(request);
    }

    @Override
    public Mono<PaymentResponseWrapper<PaymentResponse>> cancel(PaymentRequest request) {
        if (null == next) {
            return payment.cancel(request);
        }
        return next.cancel(request);
    }

    @Override
    public Mono<PaymentResponseWrapper<RefundPaymentResponse>> refund(RefundPaymentRequest request) {
        if (null == next) {
            return payment.refund(request);
        }
        return next.refund(request);
    }

    @Override
    public Mono<PaymentResponseWrapper<RefundPaymentResponse>> fetchRefund(FetchRefundPaymentRequest request) {
        if (null == next) {
            return payment.fetchRefund(request);
        }
        return next.fetchRefund(request);
    }

    @Override
    public Mono<AsyncNotifyResponse> paymentAsyncNotify(ServerRequest request) {
        if (null == next) {
            return payment.paymentAsyncNotify(request);
        }
        return next.paymentAsyncNotify(request);
    }

    @Override
    public Mono<AsyncNotifyResponse> refundAsyncNotify(ServerRequest request) {
        if (null == next) {
            return payment.refundAsyncNotify(request);
        }
        return next.refundAsyncNotify(request);
    }

}
