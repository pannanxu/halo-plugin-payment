package io.mvvm.halo.plugins.payment.sdk;

import io.mvvm.halo.plugins.payment.sdk.request.PaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.request.CreatePaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.response.CreatePaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentInfo;
import reactor.core.publisher.Mono;
import run.halo.app.extension.Ref;

/**
 * IPayment.
 *
 * @author: pan
 **/
public interface IPayment {
    /**
     * @return 支付 extension 信息
     */
    Ref type();

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
                .map(response -> new PaymentResponseWrapper<>(response, type()));
    }

    /**
     * 取消支付订单
     */
    default Mono<PaymentResponseWrapper<PaymentResponse>> cancel(PaymentRequest request) {
        return getOperator().cancel(request)
                .map(response -> new PaymentResponseWrapper<>(response, type()));
    }

    /**
     * 支付订单退款
     */
    default Mono<PaymentResponseWrapper<PaymentResponse>> refund(PaymentRequest request) {
        return getOperator().refund(request)
                .map(response -> new PaymentResponseWrapper<>(response, type()));
    }

}
