package io.mvvm.halo.plugins.payment.sdk;

import io.mvvm.halo.plugins.payment.sdk.request.CreatePaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.request.PaymentRequest;
import io.mvvm.halo.plugins.payment.sdk.response.AsyncNotifyResponse;
import io.mvvm.halo.plugins.payment.sdk.response.CreatePaymentResponse;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentInfo;
import io.mvvm.halo.plugins.payment.sdk.response.PaymentResponse;
import org.springframework.web.reactive.function.server.ServerRequest;
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

    /**
     * @return 支付方式的状态是否可用
     */
    boolean status();

    /**
     * 创建支付订单
     */
    Mono<PaymentResponseWrapper<CreatePaymentResponse>> create(CreatePaymentRequest request);

    /**
     * 查询支付订单信息
     */
    Mono<PaymentResponseWrapper<PaymentInfo>> fetch(PaymentRequest request);

    /**
     * 取消支付订单
     */
    Mono<PaymentResponseWrapper<PaymentResponse>> cancel(PaymentRequest request);

    /**
     * 支付订单退款
     */
    Mono<PaymentResponseWrapper<PaymentResponse>> refund(PaymentRequest request);

    /**
     * 支付异步通知
     */
    default Mono<AsyncNotifyResponse> paymentAsyncNotify(ServerRequest request) {
        return Mono.empty();
    }

    /**
     * 退款异步通知
     */
    default Mono<AsyncNotifyResponse> refundAsyncNotify(ServerRequest request) {
        return Mono.empty();
    }


}
