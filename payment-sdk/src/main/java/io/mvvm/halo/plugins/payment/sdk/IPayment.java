package io.mvvm.halo.plugins.payment.sdk;

import io.mvvm.halo.plugins.payment.sdk.exception.CancelException;
import io.mvvm.halo.plugins.payment.sdk.exception.FetchException;
import io.mvvm.halo.plugins.payment.sdk.exception.RefundException;
import io.mvvm.halo.plugins.payment.sdk.request.CreatePaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.PaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.response.CreatePaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.ErrorResponse;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentInfo;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentResponse;
import reactor.core.publisher.Mono;

/**
 * IPayment.
 *
 * @author: pan
 **/
public interface IPayment {
    /**
     * @return 支付 extension 信息
     */
    PaymentDescriptor getDescriptor();

    IPaymentOperator getOperator();

    /**
     * 创建支付订单
     */
    Mono<PaymentResponseWrapper<CreatePaymentResponse>> create(CreatePaymentRequest request);

    /**
     * 查询支付订单信息
     */
    default Mono<PaymentResponseWrapper<PaymentInfo>> fetch(PaymentRequest request) {
        return getOperator().fetch(request)
                .onErrorResume(FetchException.class, ex -> {
                    PaymentInfo error = new PaymentInfo().setSuccess(false);
                    error.setError(ex.getMessage());
                    error.setCode(ex.getCode());
                    error.setOutTradeNo(request.getOutTradeNo());
                    return Mono.just(error);
                })
                .onErrorResume(ex -> {
                    PaymentInfo error = new PaymentInfo().setSuccess(false);
                    error.setError(ex.getMessage());
                    error.setOutTradeNo(request.getOutTradeNo());
                    return Mono.just(error);
                })
                .map(response -> new PaymentResponseWrapper<>(response, getDescriptor()));
    }

    /**
     * 取消支付订单
     */
    default Mono<PaymentResponseWrapper<PaymentResponse>> cancel(PaymentRequest request) {
        return getOperator().cancel(request)
                .onErrorResume(CancelException.class,
                        ex -> Mono.just(ErrorResponse.error(ex.getCode(), ex.getMessage(), request.getOutTradeNo())))
                .onErrorResume(ex -> Mono.just(ErrorResponse.error(ex.getMessage())))
                .map(response -> new PaymentResponseWrapper<>(response, getDescriptor()));
    }

    /**
     * 支付订单退款
     */
    default Mono<PaymentResponseWrapper<PaymentResponse>> refund(PaymentRequest request) {
        return getOperator().refund(request)
                .onErrorResume(RefundException.class,
                        ex -> Mono.just(ErrorResponse.error(ex.getCode(), ex.getMessage(), request.getOutTradeNo())))
                .onErrorResume(ex -> Mono.just(ErrorResponse.error(ex.getMessage())))
                .map(response -> new PaymentResponseWrapper<>(response, getDescriptor()));
    }

}
