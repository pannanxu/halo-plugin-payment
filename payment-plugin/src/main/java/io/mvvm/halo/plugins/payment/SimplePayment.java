package io.mvvm.halo.plugins.payment;

import io.mvvm.halo.plugins.payment.sdk.IPayment;
import io.mvvm.halo.plugins.payment.sdk.IPaymentOperator;
import io.mvvm.halo.plugins.payment.sdk.PaymentDescriptor;
import io.mvvm.halo.plugins.payment.sdk.PaymentResponseWrapper;
import io.mvvm.halo.plugins.payment.sdk.enums.PaymentMode;
import io.mvvm.halo.plugins.payment.sdk.enums.PaymentStatus;
import io.mvvm.halo.plugins.payment.sdk.exception.CreateException;
import io.mvvm.halo.plugins.payment.sdk.request.CreatePaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.response.CreatePaymentResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
    @Getter
    private IPaymentOperator operator;

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
        return getOperator().create(request)
                .onErrorResume(CreateException.class, ex -> {
                    CreatePaymentResponse error = new CreatePaymentResponse().setSuccess(false);
                    error.setError(ex.getMessage());
                    error.setCode(ex.getCode());
                    error.setOutTradeNo(request.getOutTradeNo());
                    return Mono.just(error);
                })
                .onErrorResume(ex -> {
                    CreatePaymentResponse error = new CreatePaymentResponse().setSuccess(false);
                    error.setError(ex.getMessage());
                    error.setOutTradeNo(request.getOutTradeNo());
                    return Mono.just(error);
                })
                .map(response -> new PaymentResponseWrapper<>(response, getDescriptor()));
    }

    @Override
    public String toString() {
        return """
                SimplePayment: %s, name: %s
                """.formatted(operator, operator.getDescriptor().getName());
    }
}
