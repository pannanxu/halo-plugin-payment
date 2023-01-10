package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.IPaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.PaymentDescriptor;
import io.mvvm.halo.plugins.payment.sdk.PaymentResponseWrapper;
import io.mvvm.halo.plugins.payment.sdk.enums.PaymentMode;
import io.mvvm.halo.plugins.payment.sdk.enums.PaymentStatus;
import io.mvvm.halo.plugins.payment.sdk.exception.CancelException;
import io.mvvm.halo.plugins.payment.sdk.exception.FetchException;
import io.mvvm.halo.plugins.payment.sdk.exception.RefundException;
import io.mvvm.halo.plugins.payment.sdk.request.CreatePaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.PaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.response.AsyncNotifyResponse;
import io.mvvm.halo.plugins.payment.sdk.response.CreatePaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.ErrorResponse;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentInfo;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import run.halo.app.infra.ExternalUrlSupplier;

/**
 * SimplePayment.
 *
 * @author: pan
 **/
@Slf4j
public class SimplePayment implements IPayment {

    private final PaymentDescriptor descriptor;
    private final ExternalUrlSupplier externalUrlSupplier;
    private final IPaymentOperator operator;

    public SimplePayment(IPaymentOperator operator,
                         PaymentDescriptor descriptor,
                         ExternalUrlSupplier externalUrlSupplier) {
        this.operator = operator;
        this.descriptor = descriptor;
        this.externalUrlSupplier = externalUrlSupplier;
    }

    @Override
    public PaymentDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public boolean status() {
        return operator.status();
    }

    @Override
    public Mono<PaymentResponseWrapper<CreatePaymentResponse>> create(CreatePaymentRequest request) {
        // 0 元订单直接返回支付成功
        if (request.getTotalFee() <= 0) {
            request.setTotalFee(0);
            PaymentResponseWrapper<CreatePaymentResponse> wrapper = new PaymentResponseWrapper<>();
            wrapper.setDescriptor(descriptor);
            wrapper.setResponse(new CreatePaymentResponse()
                    .setSuccess(true)
                    .setPaymentMode(PaymentMode.none.name())
                    .setOutTradeNo(request.getOutTradeNo())
                    .setStatus(PaymentStatus.payment_successful)
                    .setTotalFee(request.getTotalFee())
                    .setExpand(request.getExpand()));
            return Mono.just(wrapper);
        }

        request.setNotifyUrl(externalUrlSupplier.get().toString(), getDescriptor().getName());
        return operator.create(request)
                .onErrorResume(FetchException.class, ex -> Mono.just(ErrorResponse.error(ex.getCode(), ex.getMessage(), CreatePaymentResponse.class)))
                .onErrorResume(ex -> Mono.just(ErrorResponse.error(ex.getMessage(), CreatePaymentResponse.class)))
                .map(response -> new PaymentResponseWrapper<>(response, getDescriptor()));
    }

    @Override
    public Mono<PaymentResponseWrapper<PaymentInfo>> fetch(PaymentRequest request) {
        return operator.fetch(request)
                .onErrorResume(FetchException.class, ex -> Mono.just(ErrorResponse.error(ex.getCode(), ex.getMessage(), PaymentInfo.class)))
                .onErrorResume(ex -> Mono.just(ErrorResponse.error(ex.getMessage(), PaymentInfo.class)))
                .map(response -> new PaymentResponseWrapper<>(response, getDescriptor()));
    }

    @Override
    public Mono<PaymentResponseWrapper<PaymentResponse>> cancel(PaymentRequest request) {
        return operator.cancel(request)
                .onErrorResume(CancelException.class, ex -> Mono.just(ErrorResponse.error(ex.getCode(), ex.getMessage(), request.getOutTradeNo())))
                .onErrorResume(ex -> Mono.just(ErrorResponse.error(ex.getMessage())))
                .map(response -> new PaymentResponseWrapper<>(response, getDescriptor()));
    }

    @Override
    public Mono<PaymentResponseWrapper<PaymentResponse>> refund(PaymentRequest request) {
        return operator.refund(request)
                .onErrorResume(RefundException.class,
                        ex -> Mono.just(ErrorResponse.error(ex.getCode(), ex.getMessage(), request.getOutTradeNo())))
                .onErrorResume(ex -> Mono.just(ErrorResponse.error(ex.getMessage())))
                .map(response -> new PaymentResponseWrapper<>(response, getDescriptor()));
    }

    @Override
    public Mono<AsyncNotifyResponse> paymentAsyncNotify(ServerRequest request) {
        return IPayment.super.paymentAsyncNotify(request);
    }

    @Override
    public Mono<AsyncNotifyResponse> refundAsyncNotify(ServerRequest request) {
        return IPayment.super.refundAsyncNotify(request);
    }

    @Override
    public String toString() {
        return """
                SimplePayment: %s, name: %s
                """.formatted(operator, operator.getDescriptor().getName());
    }
}
