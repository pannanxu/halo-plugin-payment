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
import io.mvvm.halo.plugins.payment.sdk.request.FetchRefundPaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.PaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.RefundPaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.response.AsyncNotifyResponse;
import io.mvvm.halo.plugins.payment.sdk.response.CreatePaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.ErrorResponse;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentInfo;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.RefundPaymentResponse;
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
    public Mono<Boolean> status() {
        return Mono.just(operator.status());
    }

    @Override
    public Mono<PaymentResponseWrapper<CreatePaymentResponse>> create(CreatePaymentRequest request) {
        try {
            // 0 元订单直接返回支付成功
            if (request.getMoney().isFree()) {
                PaymentResponseWrapper<CreatePaymentResponse> wrapper = new PaymentResponseWrapper<>();
                wrapper.setDescriptor(descriptor);
                wrapper.setResponse(new CreatePaymentResponse()
                        .setSuccess(true)
                        .setPaymentMode(PaymentMode.none.name())
                        .setOutTradeNo(request.getOutTradeNo())
                        .setStatus(PaymentStatus.payment_successful)
                        .setMoney(request.getMoney())
                        .setExpand(request.getExpand()));
                return Mono.just(wrapper);
            }

            request.setNotifyUrl(externalUrlSupplier.get().toString(), getDescriptor().getName());
            return operator.create(request)
                    .onErrorResume(FetchException.class, ex -> Mono.just(ErrorResponse.error(ex.getCode(), ex.getMessage(), CreatePaymentResponse.class)))
                    .onErrorResume(ex -> Mono.just(ErrorResponse.error(ex.getMessage(), CreatePaymentResponse.class)))
                    .map(response -> new PaymentResponseWrapper<>(response, getDescriptor()));
        } catch (Exception ex) {
            log.error("Payment|create|{}", ex.getMessage(), ex);
            CreatePaymentResponse error = ErrorResponse.error(ex.getMessage(), CreatePaymentResponse.class);
            return Mono.just(new PaymentResponseWrapper<>(error, descriptor));
        }
    }

    @Override
    public Mono<PaymentResponseWrapper<PaymentInfo>> fetch(PaymentRequest request) {
        try {
            return operator.fetch(request)
                    .onErrorResume(FetchException.class, ex -> Mono.just(ErrorResponse.error(ex.getCode(), ex.getMessage(), PaymentInfo.class)))
                    .onErrorResume(ex -> Mono.just(ErrorResponse.error(ex.getMessage(), PaymentInfo.class)))
                    .map(response -> new PaymentResponseWrapper<>(response, getDescriptor()));
        } catch (Exception ex) {
            log.error("Payment|fetch|{}", ex.getMessage(), ex);
            PaymentInfo error = ErrorResponse.error(ex.getMessage(), PaymentInfo.class);
            return Mono.just(new PaymentResponseWrapper<>(error, descriptor));
        }
    }

    @Override
    public Mono<PaymentResponseWrapper<PaymentResponse>> cancel(PaymentRequest request) {
        try {
            return operator.cancel(request)
                    .onErrorResume(CancelException.class, ex -> Mono.just(ErrorResponse.error(ex.getCode(), ex.getMessage(), request.getOutTradeNo())))
                    .onErrorResume(ex -> Mono.just(ErrorResponse.error(ex.getMessage())))
                    .map(response -> new PaymentResponseWrapper<>(response, getDescriptor()));
        } catch (Exception ex) {
            log.error("Payment|cancel|{}", ex.getMessage(), ex);
            PaymentResponse error = ErrorResponse.error(ex.getMessage());
            return Mono.just(new PaymentResponseWrapper<>(error, descriptor));
        }
    }

    @Override
    public Mono<PaymentResponseWrapper<RefundPaymentResponse>> refund(RefundPaymentRequest request) {
        try {
            request.setRefundNotifyUrl(externalUrlSupplier.get().toString(), getDescriptor().getName());
            return operator.refund(request)
                    .onErrorResume(RefundException.class,
                            ex -> Mono.just(ErrorResponse.error(ex.getCode(), ex.getMessage(), RefundPaymentResponse.class)))
                    .onErrorResume(ex -> Mono.just(ErrorResponse.error(ex.getMessage(), RefundPaymentResponse.class)))
                    .map(response -> new PaymentResponseWrapper<>(response, getDescriptor()));
        } catch (Exception ex) {
            log.error("Payment|refund|{}", ex.getMessage(), ex);
            RefundPaymentResponse error = ErrorResponse.error(ex.getMessage(), RefundPaymentResponse.class);
            return Mono.just(new PaymentResponseWrapper<>(error, descriptor));
        }
    }

    @Override
    public Mono<PaymentResponseWrapper<RefundPaymentResponse>> fetchRefund(FetchRefundPaymentRequest request) {
        try {
            return operator.fetchRefund(request)
                    .onErrorResume(RefundException.class,
                            ex -> Mono.just(ErrorResponse.error(ex.getCode(), ex.getMessage(), RefundPaymentResponse.class)))
                    .onErrorResume(ex -> Mono.just(ErrorResponse.error(ex.getMessage(), RefundPaymentResponse.class)))
                    .map(response -> new PaymentResponseWrapper<>(response, getDescriptor()));
        } catch (Exception ex) {
            log.error("Payment|fetchRefund|{}", ex.getMessage(), ex);
            RefundPaymentResponse error = ErrorResponse.error(ex.getMessage(), RefundPaymentResponse.class);
            return Mono.just(new PaymentResponseWrapper<>(error, descriptor));
        }
    }

    @Override
    public Mono<AsyncNotifyResponse> paymentAsyncNotify(ServerRequest request) {
        try {
            return operator.paymentAsyncNotify(request);
        } catch (Exception ex) {
            log.error("Payment|paymentAsyncNotify|{}", ex.getMessage(), ex);
            AsyncNotifyResponse error = ErrorResponse.error(ex.getMessage(), AsyncNotifyResponse.class);
            return Mono.just(error);
        }
    }

    @Override
    public Mono<AsyncNotifyResponse> refundAsyncNotify(ServerRequest request) {
        try {
            return operator.refundAsyncNotify(request);
        } catch (Exception ex) {
            log.error("Payment|refundAsyncNotify|{}", ex.getMessage(), ex);
            AsyncNotifyResponse error = ErrorResponse.error(ex.getMessage(), AsyncNotifyResponse.class);
            return Mono.just(error);
        }
    }

    @Override
    public String toString() {
        return """
                SimplePayment: %s, name: %s
                """.formatted(operator, operator.getDescriptor().getName());
    }
}
